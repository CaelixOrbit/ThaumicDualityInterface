package ThaumicDualityInterface.common.item;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.common.parts.PartEssentiaInterface;
import appeng.api.AEApi;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartEssentiaInterface extends TDIBaseItem implements IPartItem {

    public ItemPartEssentiaInterface() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("item.ThaumicDualityInterface.part_essentia_interface");

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
        GameRegistry.registerItem(this, "part_essentia_interface", ThaumicDualityInterface.MODID);
        setCreativeTab(null);
        return this;
    }

    public void registerAEPart() {
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
    }

    @Override
    protected String getIconString() {
        return ThaumicDualityInterface.MODID + ":part_essentia_interface";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
