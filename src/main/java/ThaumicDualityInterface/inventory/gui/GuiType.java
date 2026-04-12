package ThaumicDualityInterface.inventory.gui;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.ImmutableList;

import ThaumicDualityInterface.client.gui.GuiEssentiaInterface;
import ThaumicDualityInterface.client.gui.GuiEssentiaPacketDecoder;
import ThaumicDualityInterface.client.gui.container.ContainerEssentiaInterface;
import ThaumicDualityInterface.client.gui.container.ContainerEssentiaPacketDecoder;
import ThaumicDualityInterface.common.tile.TileEssentiaPacketDecoder;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;

public enum GuiType {

    DUAL_INTERFACE_ESSENTIA(new TileOrPartGuiFactory<>(IDualEssentiaHost.class) {

        @Override
        protected Object createServerGui(EntityPlayer player, IDualEssentiaHost inv) {
            return new ContainerEssentiaInterface(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, IDualEssentiaHost inv) {
            return new GuiEssentiaInterface(player.inventory, inv);
        }
    }),

    ESSENTIA_PACKET_DECODER(new TileGuiFactory<>(TileEssentiaPacketDecoder.class) {

        @Override
        protected Object createServerGui(EntityPlayer player, TileEssentiaPacketDecoder inv) {
            return new ContainerEssentiaPacketDecoder(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, TileEssentiaPacketDecoder inv) {
            return new GuiEssentiaPacketDecoder(player.inventory, inv);
        }
    });

    public static final List<GuiType> VALUES = ImmutableList.copyOf(values());
    public final IGuiFactory guiFactory;

    GuiType(IGuiFactory guiFactory) {
        this.guiFactory = guiFactory;
    }

    @Nullable
    public static GuiType getByOrdinal(int ordinal) {
        return ordinal < 0 || ordinal >= VALUES.size() ? null : VALUES.get(ordinal);
    }
}
