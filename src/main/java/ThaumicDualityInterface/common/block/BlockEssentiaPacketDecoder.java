package ThaumicDualityInterface.common.block;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.common.tile.TileEssentiaPacketDecoder;
import ThaumicDualityInterface.inventory.InventoryHandler;
import ThaumicDualityInterface.inventory.gui.GuiType;
import ThaumicDualityInterface.util.BlockPos;
import ThaumicDualityInterface.util.NameConst;
import appeng.block.AEBaseItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEssentiaPacketDecoder extends TDIBaseBlock {

    public BlockEssentiaPacketDecoder() {
        super(Material.iron, NameConst.BLOCK_ESSENTIA_PACKET_DECODER);
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
        GameRegistry.registerBlock(this, AEBaseItemBlock.class, NameConst.BLOCK_ESSENTIA_PACKET_DECODER);
        GameRegistry.registerTileEntity(TileEssentiaPacketDecoder.class, NameConst.BLOCK_ESSENTIA_PACKET_DECODER);
        setCreativeTab(thaumicenergistics.common.ThaumicEnergistics.ThETab);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List<String> toolTip,
        final boolean advancedToolTips) {
        if (isShiftKeyDown()) {
            toolTip.addAll(
                Minecraft.getMinecraft().fontRenderer
                    .listFormattedStringToWidth((NameConst.i18n(NameConst.TT_ESSENTIA_PACKET_DECODER_DESC)), 150));
        } else {
            toolTip.add(NameConst.i18n(NameConst.TT_SHIFT_FOR_MORE));
        }
    }
}
