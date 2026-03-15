package ThaumicDualityInterface.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.client.EssentiaInterfaceButtons;
import ThaumicDualityInterface.common.tile.TileEssentiaInterface;
import appeng.api.config.Settings;
import appeng.api.config.SidelessMode;
import appeng.container.AEBaseContainer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CPacketEssentiaButtonUpdate implements IMessage {

    public CPacketEssentiaButtonUpdate() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<CPacketEssentiaButtonUpdate, IMessage> {

        @Override
        public IMessage onMessage(CPacketEssentiaButtonUpdate message, MessageContext ctx) {
            final Container c = ctx.getServerHandler().playerEntity.openContainer;
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (c instanceof AEBaseContainer abc && abc.getTarget() instanceof TileEssentiaInterface tei) {
                if (tei.getDataObject() instanceof EssentiaInterfaceButtons eib) {
                    eib.setSidelessMode(
                        (SidelessMode) tei.getConfigManager()
                            .getSetting(Settings.SIDELESS_MODE));
                    ThaumicDualityInterface.proxy.netHandler.sendTo(new SPacketEssentiaButtonUpdate(eib), player);
                }
            }
            return null;
        }
    }
}
