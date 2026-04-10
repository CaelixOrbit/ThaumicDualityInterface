package ThaumicDualityInterface.loader;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import ThaumicDualityInterface.util.NameConst;
import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class TDIRecipeAndResearchLoader {

    public static final String CATEGORY = NameConst.RES_CAT_THE;

    private static IArcaneRecipe interfaceRecipe;
    private static IArcaneRecipe decoderRecipe;
    private static IRecipe p2pRecipe;

    public static void postInit() {
        registerAspects();
        registerRecipes();
        registerResearch();
    }

    private static void registerAspects() {
        AspectList interfaceAspects = new AspectList().add(Aspect.MECHANISM, 6)
            .add(Aspect.EXCHANGE, 6)
            .add(Aspect.ORDER, 8)
            .add(Aspect.ENTROPY, 4)
            .add(Aspect.ENERGY, 6)
            .add(Aspect.MAGIC, 8);
        ThaumcraftApi.registerObjectTag(
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack(),
            new int[] { 0, 32767 },
            interfaceAspects);

        AspectList decoderAspects = new AspectList().add(Aspect.METAL, 21)
            .add(Aspect.MAGIC, 8)
            .add(Aspect.CRYSTAL, 6)
            .add(Aspect.VOID, 6)
            .add(Aspect.MIND, 4)
            .add(Aspect.ENERGY, 4);
        ThaumcraftApi.registerObjectTag(
            ItemAndBlockHolder.BLOCK_ESSENTIA_PACKET_DECODER.stack(),
            new int[] { 0, 32767 },
            decoderAspects);
    }

    private static void registerRecipes() {
        ItemStack thaumium = new ItemStack(ConfigItems.itemResource, 1, 2);
        ItemStack salisMundus = new ItemStack(ConfigItems.itemResource, 1, 14);
        ItemStack filteredTube = new ItemStack(ConfigBlocks.blockTube, 1, 1);

        Item multiPartItem = GameRegistry.findItem(NameConst.MOD_AE2, NameConst.AE2_MULTI_PART);
        Block interfaceBlock = GameRegistry.findBlock(NameConst.MOD_AE2, NameConst.AE2_INTERFACE_BLOCK);

        ItemStack logicProcessor = GameRegistry.findItemStack(NameConst.MOD_AE2, NameConst.AE2_MATERIAL, 1);
        if (logicProcessor != null) logicProcessor.setItemDamage(22);
        ItemStack calcProcessor = GameRegistry.findItemStack(NameConst.MOD_AE2, NameConst.AE2_MATERIAL, 1);
        if (calcProcessor != null) calcProcessor.setItemDamage(23);
        ItemStack glassCable = new ItemStack(multiPartItem, 1, 16);
        ItemStack p2pTunnel = new ItemStack(multiPartItem, 1, 460);
        ItemStack meInterface = new ItemStack(interfaceBlock, 1, 0);

        ItemStack essentiaProvider = GameRegistry.findItemStack(NameConst.MOD_THE, NameConst.THE_ESSENTIA_PROVIDER, 1);

        interfaceRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
            NameConst.RES_ESSENTIA_INTERFACE,
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack(),
            new AspectList().add(Aspect.ORDER, 10)
                .add(Aspect.ENTROPY, 10)
                .add(Aspect.WATER, 4)
                .add(Aspect.AIR, 6),
            "TST",
            "MLP",
            "TST",
            'T',
            thaumium,
            'S',
            salisMundus,
            'M',
            meInterface,
            'L',
            logicProcessor,
            'P',
            essentiaProvider);

        decoderRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
            NameConst.RES_ESSENTIA_INTERFACE,
            ItemAndBlockHolder.BLOCK_ESSENTIA_PACKET_DECODER.stack(),
            new AspectList().add(Aspect.ORDER, 15)
                .add(Aspect.WATER, 15),
            "TFT",
            "CIC",
            "TMT",
            'T',
            thaumium,
            'F',
            filteredTube,
            'C',
            glassCable,
            'I',
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack(),
            'M',
            calcProcessor);

        p2pRecipe = new ShapelessOreRecipe(
            ItemAndBlockHolder.PART_ESSENTIA_P2P_INTERFACE.stack(),
            p2pTunnel,
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack());
        GameRegistry.addRecipe(p2pRecipe);

        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                ItemAndBlockHolder.PART_ESSENTIA_P2P_INTERFACE.stack(),
                p2pTunnel,
                ItemAndBlockHolder.PART_ESSENTIA_INTERFACE.stack()));
    }

    private static void registerResearch() {
        AspectList interfaceResearchCost = new AspectList().add(Aspect.ORDER, 6)
            .add(Aspect.EXCHANGE, 4)
            .add(Aspect.MAGIC, 4);

        ResearchItem resInterface = new ResearchItem(
            NameConst.RES_ESSENTIA_INTERFACE,
            CATEGORY,
            interfaceResearchCost,
            -2,
            -6,
            2,
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack());

        resInterface.setParents(NameConst.RES_PARENT_ESS_PROV);
        resInterface.setSecondary();
        resInterface.setConcealed();

        resInterface
            .setPages(
                new ResearchPage(NameConst.RES_PAGE_INT_1),
                new ResearchPage(interfaceRecipe),
                new ResearchPage(decoderRecipe),
                new ResearchPage(NameConst.RES_PAGE_INT_2),
                new ResearchPage(p2pRecipe))
            .registerResearchItem();
    }
}
