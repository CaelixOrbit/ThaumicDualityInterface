package ThaumicDualityInterface.common.tile;


import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.storage.AEEssentiaStack;

import static thaumicenergistics.common.storage.AEEssentiaStackType.ESSENTIA_STACK_TYPE;

public class TileEssentiaPacketDecoder extends AENetworkTile implements IGridTickable, IAEAppEngInventory {
    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 1);
    private final BaseActionSource ownActionSource = new MachineSource(this);

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(5, 120, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall) {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack == null || !(stack.getItem() instanceof ItemEssentiaPacket)) {
            return TickRateModulation.SLEEP;
        }
        Aspect aspect = ItemEssentiaPacket.getAspect(stack);
        long amount = ItemEssentiaPacket.getEssentiaAmount(stack);
        if (aspect == null || amount <= 0) {
            inventory.setInventorySlotContents(0, null);
            return TickRateModulation.SLEEP;
        }

        AEEssentiaStack aeEssentia = ItemEssentiaPacket.getEssentiaAEStack(stack);

        IEnergyGrid energyGrid = node.getGrid().getCache(IEnergyGrid.class);
        IMEMonitor<AEEssentiaStack> essentiaGrid = (IMEMonitor<AEEssentiaStack>) node.getGrid().<IStorageGrid>getCache(IStorageGrid.class).getMEMonitor(ESSENTIA_STACK_TYPE);

        AEEssentiaStack remaining = Platform.poweredInsert(energyGrid, essentiaGrid, aeEssentia, ownActionSource);

        if (remaining != null) {
            if (remaining.getStackSize() == aeEssentia.getStackSize()) {
                inventory.setInventorySlotContents(0, ItemEssentiaPacket.newStack(aspect, remaining.getStackSize()));
                return TickRateModulation.SLOWER;
            }
            inventory.setInventorySlotContents(0, ItemEssentiaPacket.newStack(aspect, remaining.getStackSize()));
            return TickRateModulation.FASTER;
        } else {
            inventory.setInventorySlotContents(0, null);
            return TickRateModulation.SLEEP;
        }
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {
        try {
            getProxy().getTick().alertDevice(getProxy().getNode());
        } catch (GridAccessException e) {
            // NO-OP
        }
    }
}
