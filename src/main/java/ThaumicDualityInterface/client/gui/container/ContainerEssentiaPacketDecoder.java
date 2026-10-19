package ThaumicDualityInterface.client.gui.container;

import net.minecraft.entity.player.InventoryPlayer;

import ThaumicDualityInterface.common.tile.TileEssentiaPacketDecoder;
import ThaumicDualityInterface.inventory.slot.TDISlotRestrictedInput;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import appeng.container.AEBaseContainer;

public class ContainerEssentiaPacketDecoder extends AEBaseContainer {

    public ContainerEssentiaPacketDecoder(InventoryPlayer ipl, TileEssentiaPacketDecoder tile) {
        super(ipl, tile);
        addSlotToContainer(
            new TDISlotRestrictedInput(
                ItemAndBlockHolder.ESSENTIA_PACKET.stack(),
                tile.getInventory(),
                0,
                80,
                35,
                ipl));
        bindPlayerInventory(ipl, 0, 84);
    }
}
