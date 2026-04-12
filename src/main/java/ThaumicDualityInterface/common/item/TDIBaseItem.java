package ThaumicDualityInterface.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ThaumicDualityInterface.loader.IRegister;

public abstract class TDIBaseItem extends Item implements IRegister<TDIBaseItem> {

    public ItemStack stack(int size, int meta) {
        return new ItemStack(this, size, meta);
    }

    public ItemStack stack(int size) {
        return new ItemStack(this, size);
    }

    public ItemStack stack() {
        return new ItemStack(this, 1);
    }
}
