package ThaumicDualityInterface.util;

import java.io.IOException;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.IAEEssentiaTank;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class Util {

    public static void mirrorEssentiaToPacket(IInventory packet, IAEEssentiaTank essentiaTank) {
        for (int i = 0; i < essentiaTank.getSlots(); i++) {
            AEEssentiaStack essentia = essentiaTank.getEssentiaInSlot(i);
            if (essentia == null) {
                packet.setInventorySlotContents(i, null);
            } else {
                packet.setInventorySlotContents(i, ItemEssentiaPacket.newDisplayStack(essentia.getAspect()));
            }
        }
    }

    public static AEEssentiaStack getEssentiaFromItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        AEEssentiaStack essentia = null;
        if (stack.getItem() instanceof ItemEssentiaPacket) {
            essentia = ItemEssentiaPacket.getEssentiaAEStack(stack);
        } else if (stack.getItem() instanceof IEssentiaContainerItem) {
            IEssentiaContainerItem container = (IEssentiaContainerItem) stack.getItem();
            AspectList aspects = container.getAspects(stack);
            if (aspects != null && aspects.size() > 0) {
                Aspect[] aspectArray = aspects.getAspects();
                if (aspectArray != null && aspectArray.length > 0 && aspectArray[0] != null) {
                    Aspect aspect = aspectArray[0];
                    long amount = aspects.getAmount(aspect);
                    essentia = new AEEssentiaStack(aspect);
                    if (essentia != null) {
                        essentia.setStackSize(amount);
                    }
                }
            }
        }
        if (essentia != null) {
            AEEssentiaStack essentia0 = essentia.copy();
            essentia0.setStackSize(essentia0.getStackSize() * stack.stackSize);
            return essentia0;
        }
        return null;
    }

    public static void writeEssentiaMapToBuf(Map<Integer, AEEssentiaStack> list, ByteBuf buf) throws IOException {
        buf.writeInt(list.size());
        for (Map.Entry<Integer, AEEssentiaStack> fs : list.entrySet()) {
            buf.writeInt(fs.getKey());
            if (fs.getValue() == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                fs.getValue()
                    .writeToPacket(buf);
            }
        }
    }

    public static void readEssentiaMapFromBuf(Map<Integer, AEEssentiaStack> list, ByteBuf buf) throws IOException {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            int id = buf.readInt();
            boolean hasValue = buf.readBoolean();
            if (!hasValue) {
                list.put(id, null);
            } else {
                AEEssentiaStack essentia = AEEssentiaStack.loadEssentiaStackFromPacket(buf);
                list.put(id, essentia);
            }
        }
    }
}
