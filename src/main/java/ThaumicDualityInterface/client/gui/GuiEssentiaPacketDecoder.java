package ThaumicDualityInterface.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.glodblock.github.FluidCraft;

import ThaumicDualityInterface.client.gui.container.ContainerEssentiaPacketDecoder;
import ThaumicDualityInterface.common.tile.TileEssentiaPacketDecoder;
import appeng.client.gui.AEBaseGui;
import appeng.core.localization.GuiText;

public class GuiEssentiaPacketDecoder extends AEBaseGui {

    private static final ResourceLocation TEX_BG = FluidCraft.resource("textures/gui/fluid_packet_decoder.png");

    public GuiEssentiaPacketDecoder(InventoryPlayer ipl, TileEssentiaPacketDecoder tile) {
        super(new ContainerEssentiaPacketDecoder(ipl, tile));
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        mc.getTextureManager()
            .bindTexture(TEX_BG);
        drawTexturedModalRect(offsetX, offsetY, 0, 0, 176, ySize);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        fontRendererObj
            .drawString(getGuiDisplayName(I18n.format("gui.ThaumicDualityInterface.decoder")), 8, 6, 0x404040);
        fontRendererObj.drawString(GuiText.inventory.getLocal(), 8, ySize - 94, 0x404040);
    }
}
