package ThaumicDualityInterface.proxy;

import net.minecraftforge.client.MinecraftForgeClient;

import ThaumicDualityInterface.client.render.RenderItemEssentiaPacket;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForgeClient.registerItemRenderer(ItemAndBlockHolder.ESSENTIA_PACKET, new RenderItemEssentiaPacket());
    }
}
