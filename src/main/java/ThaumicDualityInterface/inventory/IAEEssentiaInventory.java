package ThaumicDualityInterface.inventory;

public interface IAEEssentiaInventory {

    void onEssentiaInventoryChanged(IAEEssentiaTank inv, int slot);

    AEEssentiaInventory getInternalEssentia();
}
