package ThaumicDualityInterface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ThaumicDualityInterface.inventory.InventoryHandler;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import ThaumicDualityInterface.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(
    modid = ThaumicDualityInterface.MODID,
    version = Tags.VERSION,
    name = ThaumicDualityInterface.MODNAME,
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:appliedenergistics2;required-after:thaumicenergistics")
public class ThaumicDualityInterface {

    public static final String MODID = "thaumicdualityinterface";
    public static final String MODNAME = "Thaumic Duality Interface";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static ThaumicDualityInterface INSTANCE;

    @SidedProxy(
        clientSide = "ThaumicDualityInterface.proxy.ClientProxy",
        serverSide = "ThaumicDualityInterface.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        ItemAndBlockHolder.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(ThaumicDualityInterface.INSTANCE, new InventoryHandler());
        ItemAndBlockHolder.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
