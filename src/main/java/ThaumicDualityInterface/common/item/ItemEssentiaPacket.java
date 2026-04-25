package ThaumicDualityInterface.common.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import ThaumicDualityInterface.util.NameConst;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class ItemEssentiaPacket extends TDIBaseItem {

    private final int tickRate = 20;
    private final int leakInterval = 5;
    private final int leakRadius = 2;

    @SideOnly(Side.CLIENT)
    private IIcon baseIcon;

    public ItemEssentiaPacket() {
        setUnlocalizedName(NameConst.ITEM_ESSENTIA_PACKET);
        setMaxStackSize(1);
    }

    @Nullable
    public static Aspect getAspect(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound tag = stack.getTagCompound()
            .getCompoundTag("EssentiaStack");
        String aspectTag = tag.getString("AspectTag");
        return aspectTag.isEmpty() ? null : Aspect.getAspect(aspectTag);
    }

    @Nullable
    public static Aspect getAspect(@Nullable IAEItemStack stack) {
        return stack != null ? getAspect(stack.getItemStack()) : null;
    }

    @Nullable
    public static AEEssentiaStack getEssentiaAEStack(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound()
                .getCompoundTag("EssentiaStack");
            String aspectTag = tag.getString("AspectTag");
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspect != null && tag.hasKey("Amount")) {
                long amount = tag.getLong("Amount");
                return new AEEssentiaStack(aspect, amount);
            }
        }
        return null;
    }

    @Nullable
    public static AEEssentiaStack getEssentiaAEStack(@Nullable IAEItemStack stack) {
        return stack != null ? getEssentiaAEStack(stack.getItemStack()) : null;
    }

    public static boolean isDisplay(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound() || stack.getTagCompound() == null) {
            return false;
        }
        return stack.getTagCompound()
            .getBoolean("DisplayOnly");
    }

    public static void setEssentiaAmount(ItemStack stack, long amount) {
        if (stack == null || !stack.hasTagCompound()
            || !stack.getTagCompound()
                .hasKey("EssentiaStack", Constants.NBT.TAG_COMPOUND)) {
            return;
        }
        stack.getTagCompound()
            .getCompoundTag("EssentiaStack")
            .setLong("Amount", amount);
    }

    public static long getEssentiaAmount(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return 0;
        }
        return stack.getTagCompound()
            .getCompoundTag("EssentiaStack")
            .getLong("Amount");
    }

    @Nullable
    public static ItemStack newStack(@Nullable Aspect aspect, long amount) {
        if (aspect == null || amount <= 0) {
            return null;
        }
        ItemStack stack = ItemAndBlockHolder.ESSENTIA_PACKET.stack();
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound essentiaTag = new NBTTagCompound();
        essentiaTag.setString("AspectTag", aspect.getTag());
        essentiaTag.setLong("Amount", amount);
        tag.setTag("EssentiaStack", essentiaTag);
        stack.setTagCompound(tag);
        return stack;
    }

    @Nullable
    public static ItemStack newStack(@Nullable AEEssentiaStack essentia) {
        if (essentia != null && essentia.getStackSize() > 0) {
            return newStack(essentia.getAspect(), essentia.getStackSize());
        }
        return null;
    }

    @Nullable
    public static IAEItemStack newAeStack(@Nullable Aspect aspect, long amount) {
        return AEItemStack.create(newStack(aspect, amount));
    }

    @Nullable
    public static ItemStack newDisplayStack(@Nullable Aspect aspect) {
        if (aspect == null) {
            return null;
        }
        ItemStack stack = ItemAndBlockHolder.ESSENTIA_PACKET.stack();
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound essentiaTag = new NBTTagCompound();
        essentiaTag.setString("AspectTag", aspect.getTag());
        essentiaTag.setLong("Amount", 1);
        tag.setTag("EssentiaStack", essentiaTag);
        tag.setBoolean("DisplayOnly", true);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Aspect aspect = getAspect(stack);
        return aspect != null ? "tc.aspect." + aspect.getTag() : getUnlocalizedName();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Aspect aspect = getAspect(stack);
        boolean display = isDisplay(stack);
        if (aspect != null) {
            String aspectName = aspect.getName();
            if (display) {
                return aspectName;
            }
            long amount = getEssentiaAmount(stack);
            return String.format("%s, %,d", aspectName, amount);
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean flags) {
        Aspect aspect = getAspect(stack);
        boolean display = isDisplay(stack);
        if (display) return;
        if (aspect != null) {
            for (String line : NameConst.i18n(NameConst.TT_ESSENTIA_PACKET)
                .split("\\\\n")) {
                tooltip.add(EnumChatFormatting.GRAY + line);
            }
        } else {
            tooltip.add(EnumChatFormatting.RED + NameConst.i18n(NameConst.TT_INVALID_ESSENTIA));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        return this.baseIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        Aspect aspect = getAspect(stack);
        return aspect == null ? 16777215 : aspect.getColor();
    }

    @Override
    public ItemEssentiaPacket register() {
        GameRegistry.registerItem(this, NameConst.ITEM_ESSENTIA_PACKET, ThaumicDualityInterface.MODID);
        return this;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote || isDisplay(stack)) return;

        if (world.rand.nextInt(tickRate * leakInterval) == 0) {
            long currentAmount = getEssentiaAmount(stack);
            if (currentAmount > 0) {
                long loseAmount = 1;
                setEssentiaAmount(stack, currentAmount - loseAmount);

                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    int duration = tickRate * 5;
                    int amplifier = 0;
                    player.addPotionEffect(new PotionEffect(Config.potionVisExhaustID, duration, amplifier));
                }

                if (currentAmount - loseAmount <= 0) {
                    stack.stackSize = 0;
                }
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        net.minecraft.world.World world = entityItem.worldObj;
        if (world.isRemote) return false;

        ItemStack stack = entityItem.getEntityItem();
        if (isDisplay(stack)) return false;

        if (world.rand.nextInt(tickRate * leakInterval) == 0) {
            long currentAmount = getEssentiaAmount(stack);
            if (currentAmount > 0) {
                long loseAmount = 1;
                setEssentiaAmount(stack, currentAmount - loseAmount);

                int centerX = MathHelper.floor_double(entityItem.posX);
                int centerY = MathHelper.floor_double(entityItem.posY);
                int centerZ = MathHelper.floor_double(entityItem.posZ);

                int offsetX = world.rand.nextInt(2 * this.leakRadius + 1) - this.leakRadius;
                int offsetY = world.rand.nextInt(3) - 1;
                int offsetZ = world.rand.nextInt(2 * this.leakRadius + 1) - this.leakRadius;

                int targetX = centerX + offsetX;
                int targetY = centerY + offsetY;
                int targetZ = centerZ + offsetZ;

                if (world.isAirBlock(targetX, targetY, targetZ) || world.getBlock(targetX, targetY, targetZ)
                    .isReplaceable(world, targetX, targetY, targetZ)) {
                    if (world.rand.nextBoolean()) {
                        world.setBlock(targetX, targetY, targetZ, ConfigBlocks.blockFluxGoo, 0, 3);
                    } else {
                        world.setBlock(targetX, targetY, targetZ, ConfigBlocks.blockFluxGas, 0, 3);
                    }
                }

                if (currentAmount - loseAmount <= 0) {
                    entityItem.setDead();
                } else {
                    entityItem.setEntityItemStack(stack);
                }
            }
        }

        return false;
    }
}
