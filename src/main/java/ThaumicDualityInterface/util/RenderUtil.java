package ThaumicDualityInterface.util;

import net.minecraft.client.gui.Gui;
import org.jetbrains.annotations.Nullable;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class RenderUtil {

    public static void renderEssentiaIntoGui(Gui gui, int x, int y, int width, int height,
        @Nullable AEEssentiaStack essentiaStack, long capacity) {
        if (essentiaStack != null && essentiaStack.getAspect() != null && capacity > 0) {
            int hi = (int) (height * ((double) essentiaStack.getStackSize() / capacity));
            if (hi > height) {
                hi = height;
            }
            if (hi > 0) {
                int color = essentiaStack.getAspect()
                    .getColor();
                int argb = (0xFF << 24) | color;
                int topY = y + height - hi;
                Gui.drawRect(x, topY, x + width, y + height, argb);
            }
        }
    }
}
