package ThaumicDualityInterface.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.common.parts.PartEssentiaP2PInterface;
import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPartEssentiaP2PInterface extends TDIBaseItem implements IPartItem {

    private IIcon icon;

    public ItemPartEssentiaP2PInterface() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("part_essentia_p2p_interface");
    }

    public void registerAEPart() {
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack is) {
        return new PartEssentiaP2PInterface(is);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float xOffset, float yOffset, float zOffset) {
        return AEApi.instance()
            .partHelper()
            .placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    @Override
    public ItemPartEssentiaP2PInterface register() {
        GameRegistry.registerItem(this, "part_essentia_p2p_interface", ThaumicDualityInterface.MODID);
        return this;
    }

    @Override
    public IIcon getIconFromDamage(int dmg) {
        return icon;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.icon = register.registerIcon("appliedenergistics2:ItemPart.P2PTunnel");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
