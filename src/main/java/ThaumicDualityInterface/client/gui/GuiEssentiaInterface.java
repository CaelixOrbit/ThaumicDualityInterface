package ThaumicDualityInterface.client.gui;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.glodblock.github.inventory.gui.MouseRegionManager;

import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.client.gui.container.ContainerEssentiaInterface;
import ThaumicDualityInterface.common.parts.PartEssentiaInterface;
import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.inventory.gui.EssentiaTankMouseHandler;
import ThaumicDualityInterface.util.NameConst;
import ThaumicDualityInterface.util.RenderUtil;
import appeng.client.gui.GuiSub;
import appeng.core.localization.GuiText;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class GuiEssentiaInterface extends GuiSub {

    private static final ResourceLocation TEX_BG = ThaumicDualityInterface
        .resource("textures/gui/interface_essentia.png");
    private static final int TANK_X = 35, TANK_X_OFF = 18, TANK_Y = 53;
    private static final int TANK_WIDTH = 16, TANK_HEIGHT = 68;
    private final ContainerEssentiaInterface cont;
    private final MouseRegionManager mouseRegions = new MouseRegionManager(this);

    public GuiEssentiaInterface(InventoryPlayer ipl, IDualEssentiaHost tile) {
        super(new ContainerEssentiaInterface(ipl, tile));
        this.cont = (ContainerEssentiaInterface) inventorySlots;
        this.ySize = 231;
        this.addMouseRegin();
    }

    private void addMouseRegin() {
        for (int i = 0; i < 6; i++) {
            mouseRegions.addRegion(
                TANK_X + TANK_X_OFF * i,
                TANK_Y,
                TANK_WIDTH,
                TANK_HEIGHT,
                new EssentiaTankMouseHandler(
                    cont.getTile()
                        .getInternalEssentia(),
                    i));
        }
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        fontRendererObj.drawString(getGuiDisplayName(I18n.format(NameConst.GUI_ESSENTIA_INTERFACE)), 8, 6, 0x404040);
        fontRendererObj.drawString(GuiText.inventory.getLocal(), 8, ySize - 94, 0x404040);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        AEEssentiaInventory essentiaInv = cont.getTile()
            .getInternalEssentia();
        mc.getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        for (int i = 0; i < 6; i++) {
            if (!isPart()) {
                fontRendererObj.drawString(dirName(i), TANK_X + i * TANK_X_OFF + 5, 22, 0x404040);
            }
            RenderUtil.renderEssentiaIntoGui(
                this,
                TANK_X + i * TANK_X_OFF,
                TANK_Y,
                TANK_WIDTH,
                TANK_HEIGHT,
                essentiaInv.getEssentiaInSlot(i),
                essentiaInv.getMaxCapacity());
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mouseRegions.render(mouseX, mouseY);
    }

    public String dirName(int face) {
        return I18n.format(NameConst.GUI_ESSENTIA_INTERFACE + ".dir." + face);
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        mc.getTextureManager()
            .bindTexture(TEX_BG);
        drawTexturedModalRect(offsetX, offsetY, 0, 0, 176, ySize);
    }

    public void update(int id, AEEssentiaStack stack) {
        if (id >= 100) {
            cont.getTile()
                .setConfig(id - 100, stack);
        } else {
            cont.getTile()
                .setEssentiaInv(id, stack);
        }
    }

    private boolean isPart() {
        return this.cont.getTile() instanceof PartEssentiaInterface;
    }
}
