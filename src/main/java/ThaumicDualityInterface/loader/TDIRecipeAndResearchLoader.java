package ThaumicDualityInterface.loader;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class TDIRecipeAndResearchLoader {

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
}
