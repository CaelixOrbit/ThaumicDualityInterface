package ThaumicDualityInterface.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import ThaumicDualityInterface.client.gui.GuiEssentiaInterface;
import ThaumicDualityInterface.util.Util;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class SPacketEssentiaUpdate implements IMessage {

    private Map<Integer, AEEssentiaStack> list;
    private IAEItemStack itemStack;

    public SPacketEssentiaUpdate() {}

    public SPacketEssentiaUpdate(Map<Integer, AEEssentiaStack> data) {
        this.list = data;
        this.itemStack = null;
    }

    public SPacketEssentiaUpdate(Map<Integer, AEEssentiaStack> data, IAEItemStack itemStack) {
        this.list = data;
        this.itemStack = itemStack;
    }

    public SPacketEssentiaUpdate(Map<Integer, AEEssentiaStack> data, ItemStack itemStack) {
        this.list = data;
        this.itemStack = AEItemStack.create(itemStack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.list = new HashMap<>();
        try {
            Util.readEssentiaMapFromBuf(this.list, buf);
            if (buf.readBoolean()) {
                this.itemStack = AEItemStack.loadItemStackFromPacket(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            Util.writeEssentiaMapToBuf(this.list, buf);
            if (this.itemStack != null) {
                buf.writeBoolean(true);
                this.itemStack.writeToPacket(buf);
            } else {
                buf.writeBoolean(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<SPacketEssentiaUpdate, IMessage> {

        @Override
        public IMessage onMessage(SPacketEssentiaUpdate message, MessageContext ctx) {
            final GuiScreen gs = Minecraft.getMinecraft().currentScreen;
            if (gs instanceof GuiEssentiaInterface) {
                for (Map.Entry<Integer, AEEssentiaStack> e : message.list.entrySet()) {
                    ((GuiEssentiaInterface) gs).update(e.getKey(), e.getValue());
                }
            }
            return null;
        }
    }
}
