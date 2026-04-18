package ThaumicDualityInterface.common.parts;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.client.EssentiaInterfaceButtons;
import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import ThaumicDualityInterface.inventory.IAEEssentiaTank;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.loader.ItemAndBlockHolder;
import ThaumicDualityInterface.util.DualityEssentiaInterface;
import ThaumicDualityInterface.util.Util;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.ICustomButtonDataObject;
import appeng.helpers.ICustomButtonProvider;
import appeng.parts.p2p.PartP2PInterface;
import appeng.parts.p2p.PartP2PTunnel;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class PartEssentiaP2PInterface extends PartP2PInterface
    implements IDualEssentiaHost, ICustomButtonProvider, IEssentiaTransport, IAspectSource {

    private final DualityEssentiaInterface dualityEssentia = new DualityEssentiaInterface(this.getProxy(), this);
    private final AppEngInternalAEInventory config = new AppEngInternalAEInventory(this, 6);

    private ICustomButtonDataObject customButtonDataObject;

    public PartEssentiaP2PInterface(ItemStack is) {
        super(is);
        this.customButtonDataObject = new EssentiaInterfaceButtons(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getTypeTexture() {
        return ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.getBlockTextureFromSide(0);
    }

    @Override
    public PartP2PTunnel<?> applyMemoryCard(EntityPlayer player, IMemoryCard memoryCard, ItemStack is) {
        PartP2PTunnel<?> newTunnel = super.applyMemoryCard(player, memoryCard, is);
        if (Platform.isClient()) return newTunnel;
        NBTTagCompound data = memoryCard.getData(is);
        if (newTunnel instanceof PartEssentiaP2PInterface p2PInterface) {
            p2PInterface.duality.getConfigManager()
                .readFromNBT(data);
        }
        return newTunnel;
    }

    @Override
    protected void copySettings(final PartP2PTunnel<?> from) {
        if (from instanceof PartEssentiaP2PInterface fromInterface) {
            DualityInterface newDuality = this.duality;

            IConfigManager config = fromInterface.duality.getConfigManager();
            config.getSettings()
                .forEach(
                    setting -> newDuality.getConfigManager()
                        .putSetting(setting, config.getSetting(setting)));
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        TickingRequest item = duality.getTickingRequest(node);
        TickingRequest essentia = dualityEssentia.getTickingRequest(node);
        return new TickingRequest(
            Math.min(item.minTickRate, essentia.minTickRate),
            Math.max(item.maxTickRate, essentia.maxTickRate),
            item.isSleeping && essentia.isSleeping,
            true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation item = duality.tickingRequest(node, ticksSinceLastCall);
        TickRateModulation essentia = dualityEssentia.tickingRequest(node, ticksSinceLastCall);
        if (item.ordinal() >= essentia.ordinal()) {
            return item;
        } else {
            return essentia;
        }
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return dualityEssentia.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return dualityEssentia.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return dualityEssentia.canOutputTo(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        dualityEssentia.setSuction(aspect, amount);
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return dualityEssentia.getSuctionType(face);
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return dualityEssentia.getSuctionAmount(face);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return dualityEssentia.takeEssentia(aspect, amount, face);
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return dualityEssentia.addEssentia(aspect, amount, face);
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        return dualityEssentia.getEssentiaType(face);
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        return dualityEssentia.getEssentiaAmount(face);
    }

    @Override
    public int getMinimumSuction() {
        return dualityEssentia.getMinimumSuction();
    }

    @Override
    public boolean renderExtendedTube() {
        return dualityEssentia.renderExtendedTube();
    }

    @Override
    public AspectList getAspects() {
        return dualityEssentia.getAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        dualityEssentia.setAspects(aspects);
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return dualityEssentia.doesContainerAccept(tag);
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return dualityEssentia.addToContainer(tag, amount);
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return dualityEssentia.takeFromContainer(tag, amount);
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return dualityEssentia.takeFromContainer(ot);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return dualityEssentia.doesContainerContainAmount(tag, amount);
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return dualityEssentia.doesContainerContain(ot);
    }

    @Override
    public int containerContains(Aspect tag) {
        return dualityEssentia.containerContains(tag);
    }

    @Override
    public void onEssentiaInventoryChanged(IAEEssentiaTank inv, int slot) {
        saveChanges();
        getTileEntity().markDirty();
        dualityEssentia.onEssentiaInventoryChanged(inv, slot);
    }

    @Override
    public AEEssentiaInventory getInternalEssentia() {
        return dualityEssentia.getInternalEssentia();
    }

    @Override
    public DualityEssentiaInterface getDualityEssentia() {
        return dualityEssentia;
    }

    @Override
    public AppEngInternalAEInventory getConfig() {
        Util.mirrorEssentiaToPacket(config, dualityEssentia.getConfig());
        return config;
    }

    @Override
    public void setConfig(int id, AEEssentiaStack essentia) {
        if (id >= 0 && id < 6) {
            config.setInventorySlotContents(
                id,
                ItemEssentiaPacket.newDisplayStack(essentia == null ? null : essentia.getAspect()));
            dualityEssentia.getConfig()
                .setEssentiaInSlot(id, dualityEssentia.getStandardEssentia(essentia));
        }
    }

    @Override
    public void setEssentiaInv(int id, AEEssentiaStack essentia) {
        if (id >= 0 && id < 6) {
            dualityEssentia.getInternalEssentia()
                .setEssentiaInSlot(id, essentia);
        }
    }

    @Override
    public void getDrops(final List<ItemStack> drops, final boolean wrenched) {
        this.dualityEssentia.addDrops(drops);
        super.getDrops(drops, wrenched);
    }

    @Override
    public ItemStack getPrimaryGuiIcon() {
        return ItemAndBlockHolder.BLOCK_ESSENTIA_INTERFACE.stack();
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
}
