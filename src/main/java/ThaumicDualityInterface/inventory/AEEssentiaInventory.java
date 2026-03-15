package ThaumicDualityInterface.inventory;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.config.Actionable;
import appeng.core.AELog;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class AEEssentiaInventory implements IAEEssentiaTank {

    private final AEEssentiaStack[] essentias;
    private final IAEEssentiaInventory handler;
    private final int capacity;
    private final long capacityLong;
    public int lastIndex = 0;

    public AEEssentiaInventory(final IAEEssentiaInventory handler, final int slots, final int capacity) {
        this.essentias = new AEEssentiaStack[slots];
        this.handler = handler;
        this.capacity = capacity;
        this.capacityLong = capacity;
    }

    public AEEssentiaInventory(final IAEEssentiaInventory handler, final int slots, final long capacityLong) {
        this.essentias = new AEEssentiaStack[slots];
        this.handler = handler;
        this.capacity = Integer.MAX_VALUE;
        this.capacityLong = capacityLong;
    }

    public AEEssentiaInventory(final IAEEssentiaInventory handler, final int slots) {
        this(handler, slots, Integer.MAX_VALUE);
    }

    @Override
    public void setEssentiaInSlot(final int slot, final AEEssentiaStack essentia) {
        if (slot >= 0 && slot < this.getSlots()) {
            if (essentia != null && this.essentias[slot] != null
                && essentia.getAspect()
                    .equals(this.essentias[slot].getAspect())) {
                if (essentia.getStackSize() != this.essentias[slot].getStackSize()) {
                    this.essentias[slot].setStackSize(Math.min(essentia.getStackSize(), this.capacityLong));
                    this.onContentChanged(slot);
                }
            } else {
                if (essentia == null) {
                    this.essentias[slot] = null;
                } else {
                    this.essentias[slot] = essentia.copy();
                    this.essentias[slot].setStackSize(Math.min(essentia.getStackSize(), this.capacityLong));
                }
                this.onContentChanged(slot);
            }
        }
    }

    private void onContentChanged(final int slot) {
        if (this.handler != null && Platform.isServer()) {
            this.handler.onEssentiaInventoryChanged(this, slot);
        }
    }

    @Override
    public AEEssentiaStack getEssentiaInSlot(final int slot) {
        if (slot >= 0 && slot < this.getSlots()) {
            return this.essentias[slot];
        }
        return null;
    }

    @Override
    public int getSlots() {
        return this.essentias.length;
    }

    public long injectEssentia(final int slot, final Aspect aspect, final long amount, final Actionable mode) {
        if (aspect == null || amount <= 0) return 0;
        AEEssentiaStack current = this.essentias[slot];
        if (current != null && !current.getAspect()
            .equals(aspect)) {
            return 0;
        }
        long spaceLeft = this.capacityLong;
        if (current != null) {
            spaceLeft -= current.getStackSize();
        }
        long accepted = Math.min(amount, spaceLeft);
        if (mode == Actionable.MODULATE && accepted > 0) {
            if (current == null) {
                this.setEssentiaInSlot(slot, new AEEssentiaStack(aspect, accepted));
            } else {
                current.setStackSize(current.getStackSize() + accepted);
                this.onContentChanged(slot);
            }
        }
        return accepted;
    }

    public long extractEssentia(final int slot, final Aspect aspect, final long amount, final Actionable mode) {
        AEEssentiaStack current = this.essentias[slot];
        if (current == null || !current.getAspect()
            .equals(aspect) || amount <= 0) {
            return 0;
        }
        long extracted = Math.min(amount, current.getStackSize());
        if (mode == Actionable.MODULATE && extracted > 0) {
            current.setStackSize(current.getStackSize() - extracted);
            if (current.getStackSize() <= 0) {
                this.essentias[slot] = null;
            }
            this.onContentChanged(slot);
        }
        return extracted;
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        for (AEEssentiaStack stack : this.essentias) {
            if (stack != null) {
                list.add(stack.getAspect(), (int) Math.min(stack.getStackSize(), Integer.MAX_VALUE));
            }
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        for (AEEssentiaStack stack : this.essentias) {
            if (stack == null || stack.getAspect()
                .equals(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (amount == 0 || tag == null) return 0;
        int remaining = amount;
        for (int i = 0; i < this.getSlots() && remaining > 0; i++) {
            long injected = this.injectEssentia(i, tag, remaining, Actionable.MODULATE);
            remaining -= (int) injected;
        }
        return remaining;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (this.containerContains(tag) < amount) {
            return false;
        }
        int remainingToExtract = amount;
        for (int i = 0; i < this.getSlots() && remainingToExtract > 0; i++) {
            long extracted = this.extractEssentia(i, tag, remainingToExtract, Actionable.MODULATE);
            remainingToExtract -= (int) extracted;
        }
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return this.containerContains(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect aspect : ot.getAspects()) {
            if (this.containerContains(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        long total = 0;
        for (AEEssentiaStack stack : this.essentias) {
            if (stack != null && stack.getAspect()
                .equals(tag)) {
                total += stack.getStackSize();
            }
        }
        return (int) Math.min(total, Integer.MAX_VALUE);
    }

    public void writeToNBT(final NBTTagCompound data, final String name) {
        final NBTTagCompound c = new NBTTagCompound();
        this.writeToNBT(c);
        data.setTag(name, c);
    }

    private void writeToNBT(final NBTTagCompound target) {
        for (int x = 0; x < this.essentias.length; x++) {
            try {
                final NBTTagCompound c = new NBTTagCompound();
                if (this.essentias[x] != null) {
                    this.essentias[x].writeToNBT(c);
                }
                target.setTag("#" + x, c);
            } catch (final Exception ignored) {}
        }
    }

    public void readFromNBT(final NBTTagCompound data, final String name) {
        final NBTTagCompound c = data.getCompoundTag(name);
        if (c != null) {
            this.readFromNBT(c);
        }
    }

    private void readFromNBT(final NBTTagCompound target) {
        for (int x = 0; x < this.essentias.length; x++) {
            try {
                final NBTTagCompound c = target.getCompoundTag("#" + x);
                if (c != null) {
                    this.essentias[x] = AEEssentiaStack.loadStackFromNBT(c);
                }
            } catch (final Exception e) {
                this.essentias[x] = null;
                AELog.debug(e);
            }
        }
    }

    public void writeToBuf(final ByteBuf data) throws IOException {
        int essentiaMask = 0;
        for (int i = 0; i < this.essentias.length; i++) {
            if (this.essentias[i] != null) {
                essentiaMask |= 1 << i;
            }
        }
        data.writeByte(essentiaMask);
        for (AEEssentiaStack essentia : this.essentias) {
            if (essentia != null) {
                essentia.writeToPacket(data);
            }
        }
    }

    public boolean readFromBuf(final ByteBuf data) throws IOException {
        boolean changed = false;
        int essentiaMask = data.readByte();
        for (int i = 0; i < this.essentias.length; i++) {
            if ((essentiaMask & (1 << i)) != 0) {
                AEEssentiaStack essentia = AEEssentiaStack.loadEssentiaStackFromPacket(data);
                if (essentia != null) {
                    AEEssentiaStack origEssentia = this.essentias[i];
                    if (!essentia.equals(origEssentia)
                        || (origEssentia != null && essentia.getStackSize() != origEssentia.getStackSize())) {
                        this.essentias[i] = essentia;
                        changed = true;
                    }
                }
            } else if (this.essentias[i] != null) {
                this.essentias[i] = null;
                changed = true;
            }
        }
        return changed;
    }

    public long getMaxCapacity() {
        return this.capacityLong;
    }
}
