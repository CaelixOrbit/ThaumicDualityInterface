package ThaumicDualityInterface.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import appeng.tile.inventory.AppEngInternalAEInventory;

public final class DualHostSettings {

    private static final String ESSENTIA_CONFIG_KEY = "essentiaConfig";

    private DualHostSettings() {}

    public static void uploadSettings(IDualEssentiaHost host, NBTTagCompound compound) {
        if (compound.hasKey(ESSENTIA_CONFIG_KEY, NBT.TAG_COMPOUND)) {
            AppEngInternalAEInventory config = host.getConfig();
            config.readFromNBT(compound, ESSENTIA_CONFIG_KEY);
            host.getDualityEssentia()
                .loadConfigFromPacket(config);
            host.getDualityEssentia()
                .saveChanges();
        }
    }

    public static void downloadSettings(IDualEssentiaHost host, NBTTagCompound output) {
        host.getConfig()
            .writeToNBT(output, ESSENTIA_CONFIG_KEY);
    }
}
