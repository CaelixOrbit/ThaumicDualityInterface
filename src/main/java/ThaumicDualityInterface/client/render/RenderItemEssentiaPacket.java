package ThaumicDualityInterface.client.render;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class RenderItemEssentiaPacket implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        AEEssentiaStack aeStack = ItemEssentiaPacket.getEssentiaAEStack(item);
        if (aeStack == null || aeStack.getAspect() == null) return;
        Aspect aspect = aeStack.getAspect();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().renderEngine.bindTexture(aspect.getImage());
        int color = aspect.getColor();
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(0, 16, 0, 0.0, 1.0);
        t.addVertexWithUV(16, 16, 0, 1.0, 1.0);
        t.addVertexWithUV(16, 0, 0, 1.0, 0.0);
        t.addVertexWithUV(0, 0, 0, 0.0, 0.0);
        t.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
