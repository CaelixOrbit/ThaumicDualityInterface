package ThaumicDualityInterface.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class RenderItemEssentiaPacket implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        AEEssentiaStack aeStack = ItemEssentiaPacket.getEssentiaAEStack(item);
        if (aeStack == null || aeStack.getAspect() == null) return;
        Aspect aspect = aeStack.getAspect();

        GL11.glPushMatrix();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        Minecraft.getMinecraft().renderEngine.bindTexture(aspect.getImage());

        int color = aspect.getColor();
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);

        Tessellator t = Tessellator.instance;

        if (type == ItemRenderType.INVENTORY) {
            GL11.glDisable(GL11.GL_LIGHTING);
            t.startDrawingQuads();
            t.addVertexWithUV(0, 16, 0, 0.0, 1.0);
            t.addVertexWithUV(16, 16, 0, 1.0, 1.0);
            t.addVertexWithUV(16, 0, 0, 1.0, 0.0);
            t.addVertexWithUV(0, 0, 0, 0.0, 0.0);
            t.draw();
        } else {
            if (type == ItemRenderType.ENTITY) {
                GL11.glTranslated(-0.5, -0.25, 0);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            ItemRenderer.renderItemIn2D(t, 1.0F, 0.0F, 0.0F, 1.0F, 32, 32, 0.0625F);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
