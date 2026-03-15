package ThaumicDualityInterface.inventory.gui;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;

public abstract class TDITileOrPartGuiFactory<T> extends TDITileGuiFactory<T> {

    public TDITileOrPartGuiFactory(Class<T> invClass) {
        super(invClass);
    }

    @Nullable
    @Override
    protected T getInventory(TileEntity tile, ForgeDirection face) {
        if (tile instanceof IPartHost) {
            IPart part = ((IPartHost) tile).getPart(face);
            if (invClass.isInstance(part)) {
                return invClass.cast(part);
            }
        }
        return super.getInventory(tile, face);
    }
}
