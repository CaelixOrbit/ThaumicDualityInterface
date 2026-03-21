package ThaumicDualityInterface.client.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.client.textures.TDIPartsTexture;
import ThaumicDualityInterface.common.block.BlockEssentiaInterface;
import ThaumicDualityInterface.common.tile.TileEssentiaInterface;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.BlockRenderInfo;
import appeng.tile.misc.TileInterface;

public class RenderBlockEssentiaInterface extends BaseBlockRender<BlockEssentiaInterface, TileEssentiaInterface> {

    public RenderBlockEssentiaInterface() {
        super(false, 20);
    }

    @Override
    public boolean renderInWorld(final BlockEssentiaInterface block, final IBlockAccess world, final int x, final int y,
        final int z, final RenderBlocks renderer) {
        final TileInterface ti = (TileInterface) block.getTileEntity(world, x, y, z);
        final BlockRenderInfo info = block.getRendererInstance();
        if (ti != null && ti.getForward() != ForgeDirection.UNKNOWN) {
            final IIcon side = TDIPartsTexture.BlockEssentiaInterfaceAlternate_Arrow.getIcon();
            info.setTemporaryRenderIcons(
                TDIPartsTexture.BlockEssentiaInterfaceAlternate.getIcon(),
                block.getIcon(0, 0),
                side,
                side,
                side,
                side);
        }
        final boolean fz = super.renderInWorld(block, world, x, y, z, renderer);
        info.setTemporaryRenderIcon(null);
        return fz;
    }
}
