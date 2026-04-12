package ThaumicDualityInterface.network.wrapper;

import java.util.EnumMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper;
import cpw.mods.fml.common.network.simpleimpl.SimpleIndexedCodec;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelFutureListener;

public class FCNetworkWrapper {

    protected final EnumMap<Side, FMLEmbeddedChannel> channels;

    protected final FCIndexedCodec packetCodec;

    public FCNetworkWrapper(String channelName) {
        packetCodec = new FCIndexedCodec();
        channels = NetworkRegistry.INSTANCE.newChannel(channelName, packetCodec);
    }

    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
        Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, int discriminator,
        Side side) {
        registerMessage(instantiate(messageHandler), requestMessageType, discriminator, side);
    }

    static <REQ extends IMessage, REPLY extends IMessage> IMessageHandler<? super REQ, ? extends REPLY> instantiate(
        Class<? extends IMessageHandler<? super REQ, ? extends REPLY>> handler) {
        try {
            return handler.getDeclaredConstructor()
                .newInstance();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
        IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestMessageType, int discriminator,
        Side side) {
        packetCodec.addDiscriminator(discriminator, requestMessageType);
        FMLEmbeddedChannel channel = channels.get(side);
        String type = channel.findChannelHandlerNameForType(SimpleIndexedCodec.class);
        if (side == Side.SERVER) {
            addServerHandlerAfter(channel, type, messageHandler, requestMessageType);
        } else {
            addClientHandlerAfter(channel, type, messageHandler, requestMessageType);
        }
    }

    private <REQ extends IMessage, REPLY extends IMessage, NH extends INetHandler> void addServerHandlerAfter(
        FMLEmbeddedChannel channel, String type, IMessageHandler<? super REQ, ? extends REPLY> messageHandler,
        Class<REQ> requestType) {
        SimpleChannelHandlerWrapper<REQ, REPLY> handler = getHandlerWrapper(messageHandler, Side.SERVER, requestType);
        channel.pipeline()
            .addAfter(
                type,
                messageHandler.getClass()
                    .getName(),
                handler);
    }

    private <REQ extends IMessage, REPLY extends IMessage, NH extends INetHandler> void addClientHandlerAfter(
        FMLEmbeddedChannel channel, String type, IMessageHandler<? super REQ, ? extends REPLY> messageHandler,
        Class<REQ> requestType) {
        SimpleChannelHandlerWrapper<REQ, REPLY> handler = getHandlerWrapper(messageHandler, Side.CLIENT, requestType);
        channel.pipeline()
            .addAfter(
                type,
                messageHandler.getClass()
                    .getName(),
                handler);
    }

    private <REPLY extends IMessage, REQ extends IMessage> SimpleChannelHandlerWrapper<REQ, REPLY> getHandlerWrapper(
        IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Side side, Class<REQ> requestType) {
        return new SimpleChannelHandlerWrapper<REQ, REPLY>(messageHandler, side, requestType);
    }

    public Packet getPacketFrom(IMessage message) {
        return channels.get(Side.SERVER)
            .generatePacketFrom(message);
    }

    public void sendToAll(IMessage message) {
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER)
            .writeAndFlush(message)
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendTo(IMessage message, EntityPlayerMP player) {
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
            .set(player);
        channels.get(Side.SERVER)
            .writeAndFlush(message)
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllAround(IMessage message, TargetPoint point) {
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
            .set(point);
        channels.get(Side.SERVER)
            .writeAndFlush(message)
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToDimension(IMessage message, int dimensionId) {
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER)
            .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
            .set(dimensionId);
        channels.get(Side.SERVER)
            .writeAndFlush(message)
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToServer(IMessage message) {
        channels.get(Side.CLIENT)
            .attr(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT)
            .writeAndFlush(message)
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
