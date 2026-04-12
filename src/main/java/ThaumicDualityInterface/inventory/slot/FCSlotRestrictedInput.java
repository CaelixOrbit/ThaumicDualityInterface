package ThaumicDualityInterface.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.MutablePair;

import appeng.container.slot.SlotRestrictedInput;

public class FCSlotRestrictedInput extends SlotRestrictedInput {

    @FunctionalInterface
    public interface Filter {

        MutablePair<Boolean, ItemStack> isItemValid(final ItemStack inputItem);
    }

    private final Filter filter;
    private final InventoryPlayer p;
    private ItemStack validItem = null;

    public FCSlotRestrictedInput(ItemStack valid, IInventory i, int slotIndex, int x, int y, InventoryPlayer p) {
        this(
            (inputItem) -> new MutablePair<>(
                valid.getItem()
                    .getClass()
                    .isAssignableFrom(
                        inputItem.getItem()
                            .getClass()),
                valid),
            i,
            slotIndex,
            x,
            y,
            p);
        this.validItem = valid;
    }

    public FCSlotRestrictedInput(Filter filter, IInventory i, int slotIndex, int x, int y, InventoryPlayer p) {
        super(PlacableItemType.INSCRIBER_PLATE, i, slotIndex, x, y, p);
        this.filter = filter;
        this.p = p;
    }

    @Override
    public boolean isItemValid(final ItemStack inputItem) {
        MutablePair<Boolean, ItemStack> result = this.filter.isItemValid(inputItem);
        return result.left;
    }

    public boolean getAllowEdit() {
        return super.canTakeStack(this.p.player);
    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return getAllowEdit();
    }

    @Override
    public void onPickupFromSlot(final EntityPlayer p, final ItemStack i) {
        if (getAllowEdit()) {
            super.onPickupFromSlot(p, i);
        }
    }

    @Override
    public int getIcon() {
        if (validItem != null) {
            return super.getIcon();
        } else {
            return -1;
        }
    }
}
