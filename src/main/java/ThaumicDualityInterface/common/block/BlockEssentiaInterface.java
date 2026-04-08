package ThaumicDualityInterface.common.block;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.client.render.RenderBlockEssentiaInterface;
import ThaumicDualityInterface.client.textures.TDIPartsTexture;
import ThaumicDualityInterface.common.tile.TileEssentiaInterface;
import appeng.api.util.IOrientable;
import appeng.block.AEBaseItemBlock;
import appeng.core.features.AEFeature;
import appeng.core.sync.GuiBridge;
import appeng.tile.misc.TileInterface;
import appeng.util.Platform;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEssentiaInterface extends TDIBaseBlock {

    public BlockEssentiaInterface() {
        super(Material.iron, "block_essentia_interface");
        setFullBlock(true);
        setOpaque(true);
        setTileEntity(TileEssentiaInterface.class);
        setFeature(EnumSet.of(AEFeature.Core));
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected RenderBlockEssentiaInterface getRenderer() {
        return new RenderBlockEssentiaInterface();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (TDIPartsTexture tex : TDIPartsTexture.values()) {
            tex.registerIcon(iconRegister);
        }
        super.registerBlockIcons(iconRegister);
    }

    @Override
    public boolean onActivated(final World world, final int x, final int y, final int z, final EntityPlayer player,
        final int facing, final float hitX, final float hitY, final float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        final TileInterface tg = (TileInterface) this.getTileEntity(world, x, y, z);
        if (tg != null) {
            if (Platform.isServer()) {
                Platform.openGUI(player, tg, ForgeDirection.getOrientation(facing), GuiBridge.GUI_INTERFACE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        TileEssentiaInterface tile = (TileEssentiaInterface) this.getTileEntity(worldIn, x, y, z);
        if (tile != null) {
            tile.getInterfaceDuality()
                .updateRedstoneState();
        }
    }

    @Override
    protected boolean hasCustomRotation() {
        return true;
    }

    @Override
    protected void customRotateBlock(final IOrientable rotatable, final ForgeDirection axis) {
        if (rotatable instanceof TileInterface) {
            ((TileInterface) rotatable).setSide(axis);
        }
    }

    @Override
    public BlockEssentiaInterface register() {
        GameRegistry.registerBlock(this, AEBaseItemBlock.class, "block_essentia_interface");
        GameRegistry.registerTileEntity(TileEssentiaInterface.class, "block_essentia_interface");
        setCreativeTab(thaumicenergistics.common.ThaumicEnergistics.ThETab);
        return this;
    }
}
