package ThaumicDualityInterface.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import ThaumicDualityInterface.util.Util;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.OptionalSlotFakeTypeOnly;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class OptionalEssentiaSlotFakeTypeOnly extends OptionalSlotFakeTypeOnly {

    AEEssentiaInventory essentiaInv;

    public OptionalEssentiaSlotFakeTypeOnly(IInventory inv, AEEssentiaInventory essentiaInv,
        IOptionalSlotHost containerBus, int idx, int x, int y, int offX, int offY, int groupNum) {
        super(inv, containerBus, idx, x, y, offX, offY, groupNum);
        this.essentiaInv = essentiaInv;
    }

    @Override
    public void putStack(ItemStack is) {
        AEEssentiaStack essentiaStack = Util.getEssentiaFromItem(is);
        if (essentiaStack != null) {
            ItemStack tmp = ItemEssentiaPacket.newDisplayStack(essentiaStack.getAspect());
            if (essentiaInv != null) {
                AEEssentiaStack standard = essentiaStack.copy();
                standard.setStackSize(64);
                essentiaInv.setEssentiaInSlot(getSlotIndex(), standard);
            }
            super.putStack(tmp);
        } else {
            super.putStack(null);
            if (essentiaInv != null) {
                essentiaInv.setEssentiaInSlot(getSlotIndex(), null);
            }
        }
    }
}
