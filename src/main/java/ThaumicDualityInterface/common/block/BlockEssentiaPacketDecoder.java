package ThaumicDualityInterface.common.block;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.glodblock.github.util.BlockPos;

import ThaumicDualityInterface.common.tile.TileEssentiaPacketDecoder;
import ThaumicDualityInterface.inventory.InventoryHandler;
import ThaumicDualityInterface.inventory.gui.GuiType;
import appeng.block.AEBaseItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEssentiaPacketDecoder extends TDIBaseBlock {

    public BlockEssentiaPacketDecoder() {
        super(Material.iron, "block_essentia_packet_decoder");
        setFullBlock(true);
        setOpaque(true);
        setTileEntity(TileEssentiaPacketDecoder.class);
    }

    @Override
    public boolean onActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float hitX,
            float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileEssentiaPacketDecoder tile = (TileEssentiaPacketDecoder) getTileEntity(world, x, y, z);
        if (tile != null) {
            if (!world.isRemote) {
                InventoryHandler.openGui(
                        player,
                        world,
                        new BlockPos(x, y, z),
                        ForgeDirection.getOrientation(facing),
                        GuiType.ESSENTIA_PACKET_DECODER);
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockEssentiaPacketDecoder register() {
        GameRegistry.registerBlock(this, AEBaseItemBlock.class, "block_essentia_packet_decoder");
        GameRegistry.registerTileEntity(TileEssentiaPacketDecoder.class, "block_essentia_packet_decoder");
        setCreativeTab(null);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
            final boolean advancedToolTips) {
        if (isShiftKeyDown()) {
            toolTip.add(StatCollector.translateToLocal("tooltip.ThaumicDualityInterface.essentia_packet_decoder.desc"));
        } else {
            toolTip.add(StatCollector.translateToLocal("tooltip.ThaumicDualityInterface.shift_for_more"));
        }
    }
}
