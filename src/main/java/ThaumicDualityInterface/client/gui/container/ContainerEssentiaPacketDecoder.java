package ThaumicDualityInterface.client.gui.container;

import ThaumicDualityInterface.common.tile.TileEssentiaPacketDecoder;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import appeng.container.AEBaseContainer;
import com.glodblock.github.inventory.slot.FCSlotRestrictedInput;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEssentiaPacketDecoder extends AEBaseContainer {

    public ContainerEssentiaPacketDecoder(InventoryPlayer ipl, TileEssentiaPacketDecoder tile) {
        super(ipl, tile);
        // 使用你的项目中的源质封包进行限制
        addSlotToContainer(new FCSlotRestrictedInput(ItemAndBlockHolder.ESSENTIA_PACKET.stack(), tile.getInventory(), 0, 80, 35, ipl));
        bindPlayerInventory(ipl, 0, 84);
    }
}
