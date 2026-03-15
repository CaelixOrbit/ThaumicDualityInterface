package ThaumicDualityInterface.inventory.gui;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

import com.glodblock.github.inventory.gui.MouseRegionManager;

import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class EssentiaTankMouseHandler implements MouseRegionManager.Handler {

    private final AEEssentiaInventory tank;
    private final int index;

    public EssentiaTankMouseHandler(AEEssentiaInventory tank, int index) {
        this.tank = tank;
        this.index = index;
    }

    @Nullable
    @Override
    public List<String> getTooltip() {
        AEEssentiaStack essentia = tank.getEssentiaInSlot(index);
        String name = (essentia != null && essentia.getAspect() != null) ? essentia.getAspect()
            .getName() : I18n.format("gui.ThaumicDualityInterface.empty");
        return Arrays.asList(
            name,
            EnumChatFormatting.GRAY
                + String.format("%,d / %,d", essentia != null ? essentia.getStackSize() : 0L, tank.getMaxCapacity()));
    }
}
