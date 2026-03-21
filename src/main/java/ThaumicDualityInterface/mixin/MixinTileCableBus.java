package ThaumicDualityInterface.mixin;

import appeng.api.parts.IPart;
import appeng.tile.networking.TileCableBus;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

@Mixin(value = TileCableBus.class, remap = false)
public abstract class MixinTileCableBus implements IEssentiaTransport {

    @Shadow
    public abstract IPart getPart(ForgeDirection side);

    @Override
    public boolean isConnectable(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).isConnectable(face);
        }
        return false;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).canInputFrom(face);
        }
        return false;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).canOutputTo(face);
        }
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        // Suction is tricky for CableBus since it's side-dependent.
        // Usually, we delegate to all parts that are IEssentiaTransport.
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = getPart(side);
            if (part instanceof IEssentiaTransport) {
                ((IEssentiaTransport) part).setSuction(aspect, amount);
            }
        }
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).getSuctionType(face);
        }
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).getSuctionAmount(face);
        }
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).takeEssentia(aspect, amount, face);
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).addEssentia(aspect, amount, face);
        }
        return 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).getEssentiaType(face);
        }
        return null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        IPart part = getPart(face);
        if (part instanceof IEssentiaTransport) {
            return ((IEssentiaTransport) part).getEssentiaAmount(face);
        }
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }
}
