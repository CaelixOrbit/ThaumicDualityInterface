package ThaumicDualityInterface.proxy;

import com.glodblock.github.network.wrapper.FCNetworkWrapper;

import ThaumicDualityInterface.Config;
import ThaumicDualityInterface.Tags;
import ThaumicDualityInterface.ThaumicDualityInterface;
import ThaumicDualityInterface.network.CPacketEssentiaButtonUpdate;
import ThaumicDualityInterface.network.CPacketSwitchEssentiaGuis;
import ThaumicDualityInterface.network.SPacketEssentiaButtonUpdate;
import ThaumicDualityInterface.network.SPacketEssentiaUpdate;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public FCNetworkWrapper netHandler = new FCNetworkWrapper(ThaumicDualityInterface.MODID);

    public void preInit(FMLPreInitializationEvent event) {
        int packetId = 0;
        netHandler.registerMessage(
            CPacketEssentiaButtonUpdate.Handler.class,
            CPacketEssentiaButtonUpdate.class,
            packetId++,
            Side.SERVER);
        netHandler.registerMessage(
            SPacketEssentiaButtonUpdate.Handler.class,
            SPacketEssentiaButtonUpdate.class,
            packetId++,
            Side.CLIENT);
        netHandler.registerMessage(
            CPacketSwitchEssentiaGuis.Handler.class,
            CPacketSwitchEssentiaGuis.class,
            packetId++,
            Side.SERVER);
        netHandler
            .registerMessage(SPacketEssentiaUpdate.Handler.class, SPacketEssentiaUpdate.class, packetId++, Side.CLIENT);
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        ThaumicDualityInterface.LOG.info(Config.greeting);
        ThaumicDualityInterface.LOG.info("I am ThaumicDualityInterface at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
