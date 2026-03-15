package ThaumicDualityInterface.client.gui.container;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.inventory.slot.OptionalEssentiaSlotFakeTypeOnly;
import ThaumicDualityInterface.network.SPacketEssentiaUpdate;
import appeng.container.ContainerSubGui;
import appeng.container.slot.IOptionalSlotHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import thaumicenergistics.common.storage.AEEssentiaStack;

import java.util.HashMap;
import java.util.Map;

public class ContainerEssentiaInterface extends ContainerSubGui implements IOptionalSlotHost {

    private final IDualEssentiaHost tile;

    public ContainerEssentiaInterface(InventoryPlayer ipl, IDualEssentiaHost tile) {
        super(ipl, tile);
        this.tile = tile;
        IInventory inv = tile.getConfig();
        final int y = 35;
        final int x = 35;
        for (int i = 0; i < 6; i++) {
            addSlotToContainer(
                new OptionalEssentiaSlotFakeTypeOnly(
                    inv,
                    tile.getDualityEssentia()
                        .getConfig(),
                    this,
                    i,
                    x,
                    y,
                    i,
                    0,
                    0));
        }
        bindPlayerInventory(ipl, 0, 149);
    }

    public IDualEssentiaHost getTile() {
        return tile;
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return idx >= 0 && idx < 6;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        Map<Integer, AEEssentiaStack> tmp = new HashMap<>();
        for (int i = 0; i < tile.getInternalEssentia()
            .getSlots(); i++) {
            tmp.put(
                i,
                tile.getInternalEssentia()
                    .getEssentiaInSlot(i));
        }
        for (int i = 0; i < tile.getConfig()
            .getSizeInventory(); i++) {
            tmp.put(
                i + 100,
                ItemEssentiaPacket.getEssentiaAEStack(
                    tile.getConfig()
                        .getStackInSlot(i)));
        }
        for (final Object g : this.crafters) {
            if (g instanceof EntityPlayer) {
                ThaumicDualityInterface.proxy.netHandler.sendTo(new SPacketEssentiaUpdate(tmp), (EntityPlayerMP) g);
            }
        }
    }
}
