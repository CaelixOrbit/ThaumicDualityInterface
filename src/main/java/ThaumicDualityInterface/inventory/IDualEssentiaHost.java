package ThaumicDualityInterface.inventory;

import ThaumicDualityInterface.util.DualityEssentiaInterface;
import appeng.tile.inventory.AppEngInternalAEInventory;
import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.api.tiles.IEssentiaTransportWithSimulate;
import thaumicenergistics.common.storage.AEEssentiaStack;

public interface IDualEssentiaHost extends IAspectContainer, IEssentiaTransportWithSimulate, IAEEssentiaInventory {

    DualityEssentiaInterface getDualityEssentia();

    AppEngInternalAEInventory getConfig();

    void setConfig(int id, AEEssentiaStack essentia);

    void setEssentiaInv(int id, AEEssentiaStack essentia);
}
