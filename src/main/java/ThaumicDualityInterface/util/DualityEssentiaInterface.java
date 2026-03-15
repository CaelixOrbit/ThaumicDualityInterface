package ThaumicDualityInterface.util;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.AEEssentiaInventory;
import ThaumicDualityInterface.inventory.IAEEssentiaInventory;
import ThaumicDualityInterface.inventory.IAEEssentiaTank;
import appeng.api.config.Actionable;
import appeng.api.config.Upgrades;
import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.util.IConfigManager;
import appeng.core.settings.TickRates;
import appeng.helpers.IInterfaceHost;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.storage.MEMonitorPassThrough;
import appeng.me.storage.NullInventory;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumicenergistics.common.storage.AEEssentiaStack;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;
import static thaumicenergistics.common.storage.AEEssentiaStackType.ESSENTIA_STACK_TYPE;

public class DualityEssentiaInterface implements IGridTickable, IStorageMonitorable, IAEEssentiaInventory,
    IUpgradeableHost, IConfigManagerHost, IAspectContainer, IEssentiaTransport {

    public static final int NUMBER_OF_TANKS = 6;
    public static final long TANK_CAPACITY = 64;
    private final ConfigManager cm = new ConfigManager(this);
    private final AENetworkProxy gridProxy;
    private final IInterfaceHost iHost;
    private final BaseActionSource mySource;
    private final AEEssentiaInventory craftingBuffer = new AEEssentiaInventory(this, 10, Integer.MAX_VALUE);
    private final AEEssentiaInventory tanks = new AEEssentiaInventory(this, NUMBER_OF_TANKS, (int) TANK_CAPACITY);
    private final AEEssentiaInventory config = new AEEssentiaInventory(this, NUMBER_OF_TANKS, Integer.MAX_VALUE);
    private final AEEssentiaStack[] requireWork;
    private final Map<IAEStackType<?>, MEMonitorPassThrough<?>> monitorMap;
    private boolean hasConfig = false;
    private int isWorking = -1;
    private boolean resetConfigCache = true;

    public DualityEssentiaInterface(final AENetworkProxy networkProxy, final IInterfaceHost ih) {
        this.gridProxy = networkProxy;
        this.gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
        this.iHost = ih;
        this.mySource = new MachineSource(this.iHost);
        this.monitorMap = new IdentityHashMap<>();
        for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
            MEMonitorPassThrough<?> monitor = new MEMonitorPassThrough(new NullInventory<>(), type);
            monitor.setChangeSource(mySource);
            this.monitorMap.put(type, monitor);
        }
        this.requireWork = new AEEssentiaStack[NUMBER_OF_TANKS];
        for (int i = 0; i < NUMBER_OF_TANKS; ++i) {
            this.requireWork[i] = null;
        }
    }

    public AEEssentiaInventory getCraftingBuffer() {
        return this.craftingBuffer;
    }

    public void injectIntoCraftingBuffer(AEEssentiaStack essentia) {
        if (essentia == null || essentia.getStackSize() <= 0) return;
        long remaining = essentia.getStackSize();
        for (int i = 0; i < this.craftingBuffer.getSlots(); i++) {
            AEEssentiaStack existing = this.craftingBuffer.getEssentiaInSlot(i);
            if (existing != null && existing.getAspect() == essentia.getAspect()) {
                existing.incStackSize(remaining);
                return;
            }
        }
        for (int i = 0; i < this.craftingBuffer.getSlots(); i++) {
            if (this.craftingBuffer.getEssentiaInSlot(i) == null) {
                this.craftingBuffer.setEssentiaInSlot(i, essentia.copy());
                return;
            }
        }
    }

    public AEEssentiaStack getStandardEssentia(AEEssentiaStack essentia) {
        if (essentia == null) {
            return null;
        } else {
            return new AEEssentiaStack(essentia.getAspect(), 1);
        }
    }

    public AEEssentiaStack getStandardEssentia(Aspect aspect) {
        if (aspect == null) {
            return null;
        } else {
            return new AEEssentiaStack(aspect, 1);
        }
    }

    public void loadConfigFromPacket(IInventory packet) {
        for (int i = 0; i < packet.getSizeInventory(); i++) {
            Aspect aspect = ItemEssentiaPacket.getAspect(packet.getStackInSlot(i));
            config.setEssentiaInSlot(i, this.getStandardEssentia(aspect));
        }
    }

    public void onChannelStateChange(final MENetworkChannelsChanged c) {
        this.notifyNeighbors();
    }

    public void onPowerStateChange(final MENetworkPowerStatusChange c) {
        this.notifyNeighbors();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void gridChanged() {
        try {
            for (var entry : this.monitorMap.entrySet()) {
                MEMonitorPassThrough<?> monitor = entry.getValue();
                IMEMonitor internal = this.gridProxy.getStorage()
                    .getMEMonitor(entry.getKey());
                monitor.setInternal(internal);
            }
        } catch (final GridAccessException gae) {
            for (var monitor : this.monitorMap.values()) {
                monitor.setInternal(new NullInventory<>());
            }
        }
        this.notifyNeighbors();
    }

    public void writeToNBT(NBTTagCompound data) {
        this.tanks.writeToNBT(data, "storage");
        this.config.writeToNBT(data, "config");
        if (this.craftingBuffer != null) {
            this.craftingBuffer.writeToNBT(data, "craftingBuffer");
        }
    }

    public void readFromNBT(NBTTagCompound data) {
        this.config.readFromNBT(data, "config");
        this.tanks.readFromNBT(data, "storage");
        if (this.craftingBuffer != null) {
            this.craftingBuffer.readFromNBT(data, "craftingBuffer");
        }
        this.readConfig();
    }

    public AEEssentiaInventory getConfig() {
        return this.config;
    }

    public AEEssentiaInventory getTanks() {
        return this.tanks;
    }

    private IMEMonitor<AEEssentiaStack> getEssentiaGrid() {
        try {
            return (IMEMonitor<AEEssentiaStack>) gridProxy.getStorage()
                .getMEMonitor(ESSENTIA_STACK_TYPE);
        } catch (GridAccessException e) {
            return null;
        }
    }

    @Override
    public int getInstalledUpgrades(Upgrades u) {
        return 0;
    }

    private boolean hasWorkToDo() {
        for (AEEssentiaStack requiredWork : this.requireWork) {
            if (requiredWork != null) {
                return true;
            }
        }
        return false;
    }

    private boolean updateStorage() {
        boolean didSomething = false;
        for (int i = 0; i < NUMBER_OF_TANKS; ++i) {
            if (this.requireWork[i] != null) {
                didSomething = this.usePlan(i) || didSomething;
            }
        }
        return didSomething;
    }

    private boolean usePlan(int slot) {
        AEEssentiaStack work = this.requireWork[slot];
        this.isWorking = slot;
        boolean changed = false;
        IMEMonitor<AEEssentiaStack> dest = getEssentiaGrid();
        if (dest == null) {
            this.isWorking = -1;
            return false;
        }
        AEEssentiaStack toStore;
        if (work.getStackSize() > 0L) {
            long simulatedFill = this.tanks
                .injectEssentia(slot, work.getAspect(), work.getStackSize(), Actionable.SIMULATE);
            if (simulatedFill != work.getStackSize()) {
                changed = true;
            } else if (dest.getStorageList()
                .findPrecise(work) != null) {
                    toStore = (AEEssentiaStack) dest.extractItems(work, Actionable.MODULATE, this.mySource);
                    if (toStore != null) {
                        changed = true;
                        long filled = this.tanks
                            .injectEssentia(slot, toStore.getAspect(), toStore.getStackSize(), Actionable.MODULATE);
                        if (filled != toStore.getStackSize()) {
                            throw new IllegalStateException("Bad attempt at managing essentia tanks. ( fill )");
                        }
                    }
                }
        } else if (work.getStackSize() < 0L) {
            toStore = work.copy();
            toStore.setStackSize(-toStore.getStackSize());
            long simulatedDrain = this.tanks
                .extractEssentia(slot, toStore.getAspect(), toStore.getStackSize(), Actionable.SIMULATE);
            if (simulatedDrain == toStore.getStackSize()) {
                AEEssentiaStack notStored = (AEEssentiaStack) dest
                    .injectItems(toStore, Actionable.MODULATE, this.mySource);
                long amountInjected = toStore.getStackSize() - (notStored == null ? 0L : notStored.getStackSize());
                if (amountInjected > 0L) {
                    changed = true;
                    long removed = this.tanks
                        .extractEssentia(slot, toStore.getAspect(), amountInjected, Actionable.MODULATE);
                    if (removed != amountInjected) {
                        throw new IllegalStateException("Bad attempt at managing essentia tanks. ( drain )");
                    }
                }
            } else {
                changed = true;
            }
        }
        if (changed) {
            this.updatePlan(slot);
        }
        this.isWorking = -1;
        return changed;
    }

    private void updatePlan(int slot) {
        AEEssentiaStack req = this.config.getEssentiaInSlot(slot);
        AEEssentiaStack stored = this.tanks.getEssentiaInSlot(slot);
        AEEssentiaStack work;
        if (req == null && stored != null && stored.getStackSize() > 0L) {
            work = stored.copy();
            this.requireWork[slot] = work.setStackSize(-work.getStackSize());
        } else {
            if (req != null) {
                if (stored == null || stored.getStackSize() == 0L) {
                    this.requireWork[slot] = req.copy();
                    this.requireWork[slot].setStackSize(TANK_CAPACITY);
                    return;
                }
                if (!req.getAspect()
                    .equals(stored.getAspect())) {
                    work = stored.copy();
                    this.requireWork[slot] = work.setStackSize(-work.getStackSize());
                    return;
                }
                if (stored.getStackSize() < TANK_CAPACITY) {
                    this.requireWork[slot] = req.copy();
                    this.requireWork[slot].setStackSize(TANK_CAPACITY - stored.getStackSize());
                    return;
                }
            }
            this.requireWork[slot] = null;
        }
    }

    @Override
    public void onEssentiaInventoryChanged(IAEEssentiaTank inventory, int slot) {
        if (this.isWorking != slot) {
            boolean had;
            if (inventory == this.config) {
                had = this.hasConfig;
                this.readConfig();
                if (had != this.hasConfig) {
                    this.resetConfigCache = true;
                    this.notifyNeighbors();
                }
            } else if (inventory == this.tanks) {
                this.saveChanges();
                had = this.hasWorkToDo();
                this.updatePlan(slot);
                boolean now = this.hasWorkToDo();
                if (had != now) {
                    try {
                        if (now) {
                            this.gridProxy.getTick()
                                .alertDevice(this.gridProxy.getNode());
                        } else {
                            this.gridProxy.getTick()
                                .sleepDevice(this.gridProxy.getNode());
                        }
                    } catch (GridAccessException ignored) {}
                }
            }
        }
    }

    @Override
    public AEEssentiaInventory getInternalEssentia() {
        return this.tanks;
    }

    private void readConfig() {
        this.hasConfig = false;
        for (int i = 0; i < this.config.getSlots(); ++i) {
            if (this.config.getEssentiaInSlot(i) != null) {
                this.hasConfig = true;
                break;
            }
        }
        boolean had = this.hasWorkToDo();
        for (int x = 0; x < NUMBER_OF_TANKS; ++x) this.updatePlan(x);
        boolean has = this.hasWorkToDo();
        if (had != has) {
            try {
                if (has) this.gridProxy.getTick()
                    .alertDevice(this.gridProxy.getNode());
                else this.gridProxy.getTick()
                    .sleepDevice(this.gridProxy.getNode());
            } catch (GridAccessException ignored) {}
        }
        this.notifyNeighbors();
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        IMEMonitor<AEEssentiaStack> grid = getEssentiaGrid();
        if (grid == null || tag == null || amount <= 0) return amount;
        AEEssentiaStack toAdd = new AEEssentiaStack(tag, amount);
        AEEssentiaStack leftover = grid.injectItems(toAdd, Actionable.MODULATE, this.mySource);
        return leftover == null ? 0 : (int) leftover.getStackSize();
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return false;
        if (!this.doesContainerContainAmount(tag, amount)) return false;
        int remaining = amount;
        if (this.craftingBuffer != null) {
            for (int i = 0; i < this.craftingBuffer.getSlots(); i++) {
                AEEssentiaStack stack = this.craftingBuffer.getEssentiaInSlot(i);
                if (stack != null && stack.getAspect() == tag) {
                    int extractable = (int) Math.min(remaining, stack.getStackSize());
                    stack.decStackSize(extractable);
                    if (stack.getStackSize() <= 0) this.craftingBuffer.setEssentiaInSlot(i, null);
                    remaining -= extractable;
                    if (remaining <= 0) {
                        this.notifyNeighbors();
                        return true;
                    }
                }
            }
        }
        if (remaining > 0) {
            boolean success = this.tanks.takeFromContainer(tag, remaining);
            if (success) this.notifyNeighbors();
            return success;
        }
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return this.tanks.takeFromContainer(ot);
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        if (tag == null) return 0;
        int count = 0;
        if (this.craftingBuffer != null) {
            for (int i = 0; i < this.craftingBuffer.getSlots(); i++) {
                AEEssentiaStack stack = this.craftingBuffer.getEssentiaInSlot(i);
                if (stack != null && stack.getAspect() == tag) count += stack.getStackSize();
            }
        }
        count += this.tanks.containerContains(tag);
        return count;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return this.containerContains(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return this.tanks.doesContainerContain(ot);
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.craftingBuffer != null) {
            for (int i = 0; i < this.craftingBuffer.getSlots(); i++) {
                AEEssentiaStack stack = this.craftingBuffer.getEssentiaInSlot(i);
                if (stack != null && stack.getStackSize() > 0 && stack.getAspect() != null) {
                    list.add(stack.getAspect(), (int) stack.getStackSize());
                }
            }
        }
        AspectList tankList = this.tanks.getAspects();
        if (tankList != null && tankList.getAspects() != null) {
            for (Aspect aspect : tankList.getAspects()) {
                if (aspect != null) {
                    list.add(aspect, tankList.getAmount(aspect));
                }
            }
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.tanks.setAspects(aspects);
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return true;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return 8;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        if (this.takeFromContainer(aspect, amount)) {
            return amount;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        int leftover = this.addToContainer(aspect, amount);
        return amount - leftover;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        if (this.craftingBuffer != null) {
            for (int i = 0; i < this.craftingBuffer.getSlots(); i++) {
                AEEssentiaStack stack = this.craftingBuffer.getEssentiaInSlot(i);
                if (stack != null && stack.getStackSize() > 0 && stack.getAspect() != null) {
                    return stack.getAspect();
                }
            }
        }
        for (int i = 0; i < this.tanks.getSlots(); i++) {
            AEEssentiaStack stack = this.tanks.getEssentiaInSlot(i);
            if (stack != null && stack.getStackSize() > 0 && stack.getAspect() != null) {
                return stack.getAspect();
            }
        }
        return null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        Aspect type = getEssentiaType(face);
        return type != null ? this.containerContains(type) : 0;
    }

    @Override
    public int getMinimumSuction() {
        return 1;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(
            TickRates.Interface.getMin(),
            TickRates.Interface.getMax(),
            !this.hasWorkToDo(),
            true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall) {
        if (!this.gridProxy.isActive()) return TickRateModulation.SLEEP;
        boolean couldDoWork = this.updateStorage();
        return this.hasWorkToDo() ? (couldDoWork ? TickRateModulation.URGENT : TickRateModulation.SLOWER)
            : TickRateModulation.SLEEP;
    }

    public void notifyNeighbors() {
        if (this.gridProxy.isActive()) {
            try {
                this.gridProxy.getTick()
                    .wakeDevice(this.gridProxy.getNode());
            } catch (GridAccessException ignored) {}
        }
        TileEntity te = this.iHost.getTileEntity();
        if (te != null && te.getWorldObj() != null) {
            Platform.notifyBlocksOfNeighbors(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
        }
    }

    public void saveChanges() {
        this.iHost.saveChanges();
    }

    @Override
    public TileEntity getTile() {
        return (TileEntity) (this.iHost instanceof TileEntity ? this.iHost : null);
    }

    @Override
    public IInventory getInventoryByName(String name) {
        return iHost.getInventoryByName(name);
    }

    public IAspectContainer getEssentiaInventoryByName(String name) {
        return name.equals("config") ? this.config : null;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.cm;
    }

    @Override
    public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {}

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        if (this.hasConfig) {
            return null;
        }
        return (IMEMonitor<IAEItemStack>) this.monitorMap.get(ITEM_STACK_TYPE);
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        if (this.hasConfig) {
            return null;
        }
        return (IMEMonitor<IAEFluidStack>) this.monitorMap.get(FLUID_STACK_TYPE);
    }

    @Nullable
    @Override
    public IMEMonitor<?> getMEMonitor(@NotNull IAEStackType<?> type) {
        if (type == ITEM_STACK_TYPE) {
            return this.getItemInventory();
        } else if (type == FLUID_STACK_TYPE) {
            return this.getFluidInventory();
        }
        if (this.hasConfig) {
            return null;
        }
        return this.monitorMap.get(type);
    }

    public void addDrops(final List<ItemStack> drops) {
        for (int i = 0; i < NUMBER_OF_TANKS; i++) {
            ItemStack is = ItemEssentiaPacket.newStack(this.tanks.getEssentiaInSlot(i));
            if (is != null) {
                drops.add(is);
            }
        }
    }
}
