package ThaumicDualityInterface.inventory;

import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.common.storage.AEEssentiaStack;

public interface IAEEssentiaTank extends IAspectContainer {

    void setEssentiaInSlot(final int slot, final AEEssentiaStack essentia);

    AEEssentiaStack getEssentiaInSlot(final int slot);

    int getSlots();
}
