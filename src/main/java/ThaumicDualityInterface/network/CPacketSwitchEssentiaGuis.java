package ThaumicDualityInterface.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.inventory.gui.GuiType;
import ThaumicDualityInterface.util.BlockPos;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import appeng.container.PrimaryGui;
import appeng.container.interfaces.IContainerSubGui;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CPacketSwitchEssentiaGuis implements IMessage {

    public CPacketSwitchEssentiaGuis() {}

    @Override
    public void fromBytes(ByteBuf byteBuf) {}

    @Override
    public void toBytes(ByteBuf byteBuf) {}

    public static class Handler implements IMessageHandler<CPacketSwitchEssentiaGuis, IMessage> {

        @Override
        public IMessage onMessage(CPacketSwitchEssentiaGuis message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Container cont = player.openContainer;
            if (cont instanceof AEBaseContainer) {
                AEBaseContainer aeBaseContainer = (AEBaseContainer) cont;
                PrimaryGui pGui = aeBaseContainer.createPrimaryGui();
                ContainerOpenContext context = aeBaseContainer.getOpenContext();
                if (context == null) {
                    return null;
                }
                TileEntity te = context.getTile();
                Object target = aeBaseContainer.getTarget();
                GuiType guiType;
                if (!(target instanceof IDualEssentiaHost)) {
                    return null;
                }
                guiType = GuiType.DUAL_INTERFACE_ESSENTIA;
                ForgeDirection side = context.getSide();
                if (side == null) {
                    side = ForgeDirection.UNKNOWN;
                }
                ThaumicDualityInterface.inventory.InventoryHandler
                    .openGui(player, player.worldObj, new BlockPos(te), side, guiType);
                Container currentContainer = player.openContainer;
                if (currentContainer instanceof IContainerSubGui) {
                    IContainerSubGui sg = (IContainerSubGui) currentContainer;
                    sg.setPrimaryGui(pGui);
                }
            }
            return null;
        }
    }
}
