package ThaumicDualityInterface.loader;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class TDIRecipeAndResearchLoader {

    private static IArcaneRecipe interfaceRecipe;
    private static IArcaneRecipe decoderRecipe;
    private static IRecipe p2pRecipe;

    public static void postInit() {
        registerAspects();
        registerRecipes();
    }

    private static void registerAspects() {
        AspectList interfaceAspects = new AspectList()
            .add(Aspect.MECHANISM, 6)
            .add(Aspect.MAGIC, 4)
            .add(Aspect.EXCHANGE, 4)
            .add(Aspect.ORDER, 3)
            .add(Aspect.VOID, 2);
        ThaumcraftApi.registerObjectTag(
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack(),
            new int[]{0, 32767},
            interfaceAspects
        );

        AspectList decoderAspects = new AspectList()
            .add(Aspect.MECHANISM, 5)
            .add(Aspect.MIND, 4)
            .add(Aspect.EXCHANGE, 3)
            .add(Aspect.WATER, 2)
            .add(Aspect.CRYSTAL, 2);
        ThaumcraftApi.registerObjectTag(
            ItemAndBlockHolder.BLOCK_ESSENTIA_PACKET_DECODER.stack(),
            new int[]{0, 32767},
            decoderAspects
        );
    }

    private static void registerRecipes() {
        ItemStack thaumium = new ItemStack(ConfigItems.itemResource, 1, 2);
        ItemStack salisMundus = new ItemStack(ConfigItems.itemResource, 1, 14);
        ItemStack filteredTube = new ItemStack(ConfigBlocks.blockTube, 1, 1);

        Item multiPartItem = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiPart");
        Item multiMaterialItem = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
        Block interfaceBlock = GameRegistry.findBlock("appliedenergistics2", "tile.BlockInterface");

        ItemStack logicProcessor = new ItemStack(multiMaterialItem, 1, 22);
        ItemStack calcProcessor = new ItemStack(multiMaterialItem, 1, 23);
        ItemStack glassCable = new ItemStack(multiPartItem, 1, 16);
        ItemStack p2pTunnel = new ItemStack(multiPartItem, 1, 460); // 真正精确的 P2P 通道！
        ItemStack meInterface = new ItemStack(interfaceBlock, 1, 0);

        ItemStack essentiaProvider = GameRegistry.findItemStack("thaumicenergistics", "thaumicenergistics.block.essentia.provider", 1);

        logicProcessor = GameRegistry.findItemStack("appliedenergistics2", "item.ItemMultiMaterial", 1);
        if (logicProcessor != null) logicProcessor.setItemDamage(22);

        calcProcessor = GameRegistry.findItemStack("appliedenergistics2", "item.ItemMultiMaterial", 1);
        if (calcProcessor != null) calcProcessor.setItemDamage(23);

        interfaceRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
            "TDI_ESSENTIA_INTERFACE",
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack(),
            new AspectList().add(Aspect.ORDER, 25).add(Aspect.AIR, 10),
            "TST",
            "MLP",
            "TST",
            'T', thaumium,
            'S', salisMundus,
            'M', meInterface,
            'L', logicProcessor,
            'P', essentiaProvider
        );

        decoderRecipe = ThaumcraftApi.addArcaneCraftingRecipe(
            "TDI_ESSENTIA_INTERFACE",
            ItemAndBlockHolder.BLOCK_ESSENTIA_PACKET_DECODER.stack(),
            new AspectList().add(Aspect.ORDER, 15).add(Aspect.WATER, 15),
            "TFT",
            "CIC",
            "TMT",
            'T', thaumium,
            'F', filteredTube,
            'C', glassCable,
            'I', ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack(),
            'M', calcProcessor
        );

        p2pRecipe = new ShapelessOreRecipe(
            ItemAndBlockHolder.PART_ESSENTIA_P2P_INTERFACE.stack(),
            p2pTunnel,
            ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack()
        );
        GameRegistry.addRecipe(p2pRecipe);

        GameRegistry.addRecipe(new ShapelessOreRecipe(
            ItemAndBlockHolder.PART_ESSENTIA_P2P_INTERFACE.stack(),
            p2pTunnel,
            ItemAndBlockHolder.PART_ESSENTIA_INTERFACE.stack()
        ));
    }
}
