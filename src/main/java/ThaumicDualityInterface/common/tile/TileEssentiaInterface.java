package ThaumicDualityInterface.common.tile;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ThaumicDualityInterface.client.EssentiaInterfaceButtons;
import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import ThaumicDualityInterface.inventory.IAEEssentiaTank;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import ThaumicDualityInterface.util.DualityEssentiaInterface;
import ThaumicDualityInterface.util.Util;
import appeng.api.config.Actionable;
import appeng.api.config.Settings;
import appeng.api.config.SidelessMode;
import appeng.api.config.Upgrades;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.util.IConfigManager;
import appeng.core.settings.TickRates;
import appeng.helpers.ICustomButtonDataObject;
import appeng.helpers.ICustomButtonProvider;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.misc.TileInterface;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.api.tiles.IEssentiaTransportWithSimulate;
import thaumicenergistics.common.integration.tc.EssentiaTransportHelper;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class TileEssentiaInterface extends TileInterface
    implements IDualEssentiaHost, ICustomButtonProvider, IAspectContainer, IEssentiaTransportWithSimulate {

    private static final int TICK_RATE_IDLE = 15, TICK_RATE_URGENT = TickRates.Interface.getMin();
    private int tickCount = 0;
    private int tickRate = TICK_RATE_IDLE;

    private final IConfigManager dualityConfigManager = getInterfaceDuality().getConfigManager();
    private final DualityEssentiaInterface essentiaDuality = new DualityEssentiaInterface(this.getProxy(), this) {

        @Override
        public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
            SidelessMode mode = (SidelessMode) dualityConfigManager.getSetting(Settings.SIDELESS_MODE);
            if (mode == SidelessMode.SIDELESS || face == ForgeDirection.UNKNOWN) {
                return super.takeEssentia(aspect, amount, face);
            }
            int slotIndex = face.ordinal();
            AEEssentiaStack stored = this.getInternalEssentia()
                .getEssentiaInSlot(slotIndex);
            if (stored != null && stored.getAspect()
                .equals(aspect) && stored.getStackSize() >= amount) {
                this.getInternalEssentia()
                    .extractEssentia(slotIndex, aspect, amount, appeng.api.config.Actionable.MODULATE);
                return amount;
            }
            return 0;
        }
    };
    private final AppEngInternalAEInventory config = new AppEngInternalAEInventory(this, 6);
    private ICustomButtonDataObject customButtonDataObject;

    public TileEssentiaInterface() {
        super.getInterfaceDuality().getConfigManager()
            .registerSetting(Settings.SIDELESS_MODE, SidelessMode.SIDELESS);
        this.customButtonDataObject = new EssentiaInterfaceButtons(true);
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        essentiaDuality.onChannelStateChange(c);
        super.stateChange(c);
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        essentiaDuality.onPowerStateChange(c);
        super.stateChange(c);
    }

    @Override
    public void gridChanged() {
        super.gridChanged();
        essentiaDuality.gridChanged();
    }

    @Override
    public DualityEssentiaInterface getDualityEssentia() {
        return essentiaDuality;
    }

    @Override
    public AEEssentiaInventory getInternalEssentia() {
        return essentiaDuality.getInternalEssentia();
    }

    @Override
    public AppEngInternalAEInventory getConfig() {
        Util.mirrorEssentiaToPacket(this.config, essentiaDuality.getConfig());
        return config;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        TickingRequest item = super.getTickingRequest(node);
        TickingRequest essentia = essentiaDuality.getTickingRequest(node);
        return new TickingRequest(
            Math.min(item.minTickRate, essentia.minTickRate),
            Math.max(item.maxTickRate, essentia.maxTickRate),
            item.isSleeping && essentia.isSleeping,
            true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation item = super.tickingRequest(node, ticksSinceLastCall);
        TickRateModulation essentia = essentiaDuality.tickingRequest(node, ticksSinceLastCall);
        if (item.ordinal() >= essentia.ordinal()) {
            return item;
        } else {
            return essentia;
        }
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    protected void writeToStream(ByteBuf data) throws IOException {
        for (int i = 0; i < config.getSizeInventory(); i++) {
            ByteBufUtils.writeItemStack(data, config.getStackInSlot(i));
        }
        getInternalEssentia().writeToBuf(data);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    protected boolean readFromStream(ByteBuf data) throws IOException {
        boolean changed = false;
        for (int i = 0; i < config.getSizeInventory(); i++) {
            ItemStack stack = ByteBufUtils.readItemStack(data);
            if (!ItemStack.areItemStacksEqual(stack, config.getStackInSlot(i))) {
                config.setInventorySlotContents(i, stack);
                changed = true;
            }
        }
        essentiaDuality.loadConfigFromPacket(this.config);
        changed |= getInternalEssentia().readFromBuf(data);
        return changed;
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNBTEvent(NBTTagCompound data) {
        config.readFromNBT(data, "ConfigInv");
        essentiaDuality.loadConfigFromPacket(this.config);
        getInternalEssentia().readFromNBT(data, "EssentiaInv");
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public NBTTagCompound writeToNBTEvent(NBTTagCompound data) {
        config.writeToNBT(data, "ConfigInv");
        getInternalEssentia().writeToNBT(data, "EssentiaInv");
        return data;
    }

    @Nullable
    protected ItemStack getItemFromTile(final Object obj) {
        if (obj instanceof TileEssentiaInterface) {
            return ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack();
        }
        return null;
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return getInterfaceDuality().getInstalledUpgrades(u);
    }

    @Override
    public AspectList getAspects() {
        return essentiaDuality.getAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        essentiaDuality.setAspects(aspects);
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return essentiaDuality.doesContainerAccept(tag);
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return essentiaDuality.addToContainer(tag, amount);
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return essentiaDuality.takeFromContainer(tag, amount);
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return essentiaDuality.takeFromContainer(ot);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return essentiaDuality.doesContainerContainAmount(tag, amount);
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return essentiaDuality.doesContainerContain(ot);
    }

    @Override
    public int containerContains(Aspect tag) {
        return essentiaDuality.containerContains(tag);
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return essentiaDuality.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return essentiaDuality.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return essentiaDuality.canOutputTo(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        essentiaDuality.setSuction(aspect, amount);
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return essentiaDuality.getSuctionType(face);
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return essentiaDuality.getSuctionAmount(face);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return essentiaDuality.takeEssentia(aspect, amount, face);
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face, Actionable mode) {
        long acceptedAmount = essentiaDuality.addEssentia(aspect, amount, face, mode);
        if ((mode == Actionable.MODULATE) && (acceptedAmount > 0)) {
            this.tickRate = TileEssentiaInterface.TICK_RATE_URGENT;
        }
        return (int) acceptedAmount;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return essentiaDuality.addEssentia(aspect, amount, face);
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        return essentiaDuality.getEssentiaType(face);
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        return essentiaDuality.getEssentiaAmount(face);
    }

    @Override
    public int getMinimumSuction() {
        return essentiaDuality.getMinimumSuction();
    }

    @Override
    public boolean renderExtendedTube() {
        return essentiaDuality.renderExtendedTube();
    }

    @Override
    public void onEssentiaInventoryChanged(IAEEssentiaTank inv, int slot) {
        saveChanges();
        markForUpdate();
        essentiaDuality.onEssentiaInventoryChanged(inv, slot);
    }

    @Override
    public void setConfig(int id, AEEssentiaStack essentia) {
        if (id >= 0 && id < 6) {
            config.setInventorySlotContents(
                id,
                ItemEssentiaPacket.newDisplayStack(essentia == null ? null : essentia.getAspect()));
            essentiaDuality.getConfig()
                .setEssentiaInSlot(id, essentiaDuality.getStandardEssentia(essentia));
        }
    }

    @Override
    public void setEssentiaInv(int id, AEEssentiaStack essentia) {
        if (id >= 0 && id < 6) {
            getInternalEssentia().setEssentiaInSlot(id, essentia);
        }
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        this.essentiaDuality.addDrops(drops);
        super.getDrops(w, x, y, z, drops);
    }

    @Override
    public ItemStack getPrimaryGuiIcon() {
        return ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack();
    }

    @Override
    public void writeCustomButtonData() {}

    @Override
    public void readCustomButtonData() {}

    @SideOnly(Side.CLIENT)
    @Override
    public void initCustomButtons(int guiLeft, int guiTop, int xSize, int ySize, int xOffset, int yOffset,
        List<GuiButton> buttonList) {
        if (customButtonDataObject != null)
            customButtonDataObject.initCustomButtons(guiLeft, guiTop, xSize, ySize, xOffset, yOffset, buttonList);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean actionPerformedCustomButtons(final GuiButton btn) {
        return customButtonDataObject != null && customButtonDataObject.actionPerformedCustomButtons(btn);
    }

    @Override
    public ICustomButtonDataObject getDataObject() {
        return customButtonDataObject;
    }

    @Override
    public void setDataObject(ICustomButtonDataObject dataObject) {
        customButtonDataObject = dataObject;
    }

    @TileEvent(TileEventType.TICK)
    public void onTick() {
        // Ensure this is server side, and that 5 ticks have elapsed
        if ((!this.worldObj.isRemote) && (++this.tickCount >= this.tickRate)) {
            // Reset the tick count
            this.tickCount = 0;

            // Assume idle
            this.tickRate = TileEssentiaInterface.TICK_RATE_IDLE;

            // Take essentia from the neighbors
            EssentiaTransportHelper.INSTANCE
                .takeEssentiaFromTransportNeighbors(this, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
    }
}
