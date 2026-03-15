package ThaumicDualityInterface.common.parts;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.client.EssentiaInterfaceButtons;
import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import ThaumicDualityInterface.inventory.IAEEssentiaTank;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import ThaumicDualityInterface.util.DualityEssentiaInterface;
import ThaumicDualityInterface.util.Util;
import appeng.api.config.Upgrades;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.helpers.ICustomButtonDataObject;
import appeng.helpers.ICustomButtonProvider;
import appeng.parts.misc.PartInterface;
import appeng.tile.inventory.AppEngInternalAEInventory;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class PartEssentiaInterface extends PartInterface implements IDualEssentiaHost, ICustomButtonProvider {

    private final AppEngInternalAEInventory config = new AppEngInternalAEInventory(this, 6);
    private final DualityEssentiaInterface essentiaDuality = new DualityEssentiaInterface(this.getProxy(), this);
    private ICustomButtonDataObject customButtonDataObject;

    public PartEssentiaInterface(ItemStack is) {
        super(is);
        this.customButtonDataObject = new EssentiaInterfaceButtons(false);
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
        return essentiaDuality.getTanks();
    }

    @Override
    public AppEngInternalAEInventory getConfig() {
        Util.mirrorEssentiaToPacket(this.config, essentiaDuality.getConfig());
        return config;
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        super.writeToStream(data);
        for (int i = 0; i < config.getSizeInventory(); i++) {
            ByteBufUtils.writeItemStack(data, config.getStackInSlot(i));
        }
        getInternalEssentia().writeToBuf(data);
    }

    @Override
    public boolean readFromStream(ByteBuf data) throws IOException {
        super.readFromStream(data);
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

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        config.readFromNBT(data, "ConfigInv");
        essentiaDuality.loadConfigFromPacket(this.config);
        getInternalEssentia().readFromNBT(data, "EssentiaInv");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        config.writeToNBT(data, "ConfigInv");
        getInternalEssentia().writeToNBT(data, "EssentiaInv");
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return getInterfaceDuality().getInstalledUpgrades(u);
    }

    @Override
    public void onEssentiaInventoryChanged(IAEEssentiaTank inv, int slot) {
        saveChanges();
        getTileEntity().markDirty();
        essentiaDuality.onEssentiaInventoryChanged(inv, slot);
    }

    @Override
    public void setConfig(int id, AEEssentiaStack essentia) {
        if (id >= 0 && id < 6) {
            this.config.setInventorySlotContents(
                id,
                ItemEssentiaPacket.newDisplayStack(essentia == null ? null : essentia.getAspect()));
            this.essentiaDuality.getConfig()
                .setEssentiaInSlot(id, this.essentiaDuality.getStandardEssentia(essentia));
        }
    }

    @Override
    public void setEssentiaInv(int id, AEEssentiaStack essentia) {
        if (id >= 0 && id < 6) {
            this.getInternalEssentia()
                .setEssentiaInSlot(id, essentia);
        }
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        TickingRequest item = super.getTickingRequest(node);
        TickingRequest essentia = essentiaDuality.getTickingRequest(node);
        return new TickingRequest(
            Math.min(item.minTickRate, essentia.minTickRate),
            Math.max(item.maxTickRate, essentia.maxTickRate),
            item.isSleeping && essentia.isSleeping,
            true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall) {
        TickRateModulation item = super.tickingRequest(node, TicksSinceLastCall);
        TickRateModulation essentia = essentiaDuality.tickingRequest(node, TicksSinceLastCall);
        if (item.ordinal() >= essentia.ordinal()) {
            return item;
        } else {
            return essentia;
        }
    }

    @Override
    public ItemStack getPrimaryGuiIcon() {
        return ItemAndBlockHolder.PART_ESSENTIA_INTERFACE.stack();
    }

    @Override
    public void writeCustomButtonData() {}

    @Override
    public void readCustomButtonData() {}

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

    @Override
    public AspectList getAspects() {
        return new AspectList();
    }

    @Override
    public void setAspects(AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return false;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return false;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection forgeDirection) {
        return null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection forgeDirection) {
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
