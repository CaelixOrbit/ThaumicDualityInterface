package ThaumicDualityInterface.loader;

import ThaumicDualityInterface.common.block.BlockEssentiaInterface;
import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.common.item.ItemPartEssentiaInterface;

public class ItemAndBlockHolder {

    public static BlockEssentiaInterface BLOCK_ESSENTIA_BLOCK;
    public static ItemPartEssentiaInterface PART_ESSENTIA_INTERFACE;
    public static ItemEssentiaPacket ESSENTIA_PACKET;

    public static void preInit() {
        PART_ESSENTIA_INTERFACE = new ItemPartEssentiaInterface();
        PART_ESSENTIA_INTERFACE.register();

        ESSENTIA_PACKET = new ItemEssentiaPacket();
        ESSENTIA_PACKET.register();
    }

    public static void init() {
        BLOCK_ESSENTIA_BLOCK = new BlockEssentiaInterface();
        BLOCK_ESSENTIA_BLOCK.register();

        PART_ESSENTIA_INTERFACE.registerAEPart();
    }
}
