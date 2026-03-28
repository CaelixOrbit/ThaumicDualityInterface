package ThaumicDualityInterface.loader;

import ThaumicDualityInterface.common.block.BlockEssentiaInterface;
import ThaumicDualityInterface.common.block.BlockEssentiaPacketDecoder;
import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.common.item.ItemPartEssentiaInterface;

public class ItemAndBlockHolder {

    public static BlockEssentiaInterface BLOCK_ESSENTIAL_INTERFACE;
    public static BlockEssentiaPacketDecoder BLOCK_ESSENTIA_PACKET_DECODER;
    public static ItemPartEssentiaInterface PART_ESSENTIA_INTERFACE;
    public static ItemEssentiaPacket ESSENTIA_PACKET;

    public static void preInit() {
        PART_ESSENTIA_INTERFACE = new ItemPartEssentiaInterface();
        PART_ESSENTIA_INTERFACE.register();

        ESSENTIA_PACKET = new ItemEssentiaPacket();
        ESSENTIA_PACKET.register();
    }

    public static void init() {
        BLOCK_ESSENTIAL_INTERFACE = new BlockEssentiaInterface();
        BLOCK_ESSENTIAL_INTERFACE.register();
        BLOCK_ESSENTIA_PACKET_DECODER = new BlockEssentiaPacketDecoder();
        BLOCK_ESSENTIA_PACKET_DECODER.register();

        PART_ESSENTIA_INTERFACE.registerAEPart();
    }
}
