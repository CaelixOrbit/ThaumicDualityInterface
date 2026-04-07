package ThaumicDualityInterface.client.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import ThaumicDualityInterface.ThaumicDualityInterface;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum TDIPartsTexture {

    BlockEssentiaInterfaceAlternate_Arrow("essentia_interface_arrow"),
    BlockEssentiaInterfaceAlternate("essentia_interface_a"),
    BlockEssentiaInterface_Face("essentia_interface");

    private final String name;
    public IIcon IIcon;

    TDIPartsTexture(final String name) {
        this.name = name;
    }

    public static ResourceLocation GuiTexture(final String string) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getMissing() {
        return ((TextureMap) Minecraft.getMinecraft()
            .getTextureManager()
            .getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
    }

    public String getName() {
        return this.name;
    }

    public IIcon getIcon() {
        return this.IIcon;
    }

    public void registerIcon(final IIconRegister map) {
        this.IIcon = map.registerIcon(ThaumicDualityInterface.MODID + ":" + this.name);
    }
}
