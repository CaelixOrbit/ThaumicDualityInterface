package ThaumicDualityInterface.common.item;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.common.parts.PartEssentiaInterface;
import ThaumicDualityInterface.util.NameConst;
import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartEssentiaInterface extends TDIBaseItem implements IPartItem {

    public ItemPartEssentiaInterface() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName(NameConst.ITEM_PART_ESSENTIA_INTERFACE);

    }

    @Nullable
    @Override
    public PartEssentiaInterface createPartFromItemStack(ItemStack is) {
        return new PartEssentiaInterface(is);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float xOffset, float yOffset, float zOffset) {
        return AEApi.instance()
            .partHelper()
            .placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    @Override
    public ItemPartEssentiaInterface register() {
        GameRegistry.registerItem(this, NameConst.ITEM_PART_ESSENTIA_INTERFACE, ThaumicDualityInterface.MODID);
        setCreativeTab(thaumicenergistics.common.ThaumicEnergistics.ThETab);
        return this;
    }

    public void registerAEPart() {
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
    }

    @Override
    protected String getIconString() {
        return ThaumicDualityInterface.MODID + ":" + NameConst.BLOCK_ESSENTIA_INTERFACE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
