package ThaumicDualityInterface.common.tile;

import static thaumicenergistics.common.storage.AEEssentiaStackType.ESSENTIA_STACK_TYPE;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.helpers.Reflected;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumicenergistics.common.storage.AEEssentiaStack;

public class TileEssentiaPacketDecoder extends AENetworkTile
    implements IGridTickable, IAEAppEngInventory, IInventory, IEssentiaTransport, IAspectSource {

    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 1);
    private final BaseActionSource ownActionSource = new MachineSource(this);

    @Reflected
    public TileEssentiaPacketDecoder() {
        getProxy().setIdlePowerUsage(1.0D);
        getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    public IInventory getInventory() {
        return inventory;
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public NBTTagCompound writeToNBTEvent(NBTTagCompound data) {
        inventory.writeToNBT(data, "Inventory");
        return data;
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNBTEvent(NBTTagCompound data) {
        inventory.readFromNBT(data, "Inventory");
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(5, 120, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall) {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack == null || !(stack.getItem() instanceof ItemEssentiaPacket)) {
            return TickRateModulation.SLEEP;
        }
        Aspect aspect = ItemEssentiaPacket.getAspect(stack);
        long amount = ItemEssentiaPacket.getEssentiaAmount(stack);
        if (aspect == null || amount <= 0) {
            inventory.setInventorySlotContents(0, null);
            return TickRateModulation.SLEEP;
        }

        AEEssentiaStack aeEssentia = ItemEssentiaPacket.getEssentiaAEStack(stack);

        IEnergyGrid energyGrid = node.getGrid()
            .getCache(IEnergyGrid.class);
        IMEMonitor<AEEssentiaStack> essentiaGrid = (IMEMonitor<AEEssentiaStack>) node.getGrid()
            .<IStorageGrid>getCache(IStorageGrid.class)
            .getMEMonitor(ESSENTIA_STACK_TYPE);

        AEEssentiaStack remaining = Platform.poweredInsert(energyGrid, essentiaGrid, aeEssentia, ownActionSource);

        if (remaining != null) {
            if (remaining.getStackSize() == aeEssentia.getStackSize()) {
                inventory.setInventorySlotContents(0, ItemEssentiaPacket.newStack(aspect, remaining.getStackSize()));
                return TickRateModulation.SLOWER;
            }
            inventory.setInventorySlotContents(0, ItemEssentiaPacket.newStack(aspect, remaining.getStackSize()));
            return TickRateModulation.FASTER;
        } else {
            inventory.setInventorySlotContents(0, null);
            return TickRateModulation.SLEEP;
        }
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removedStack,
        ItemStack newStack) {
        this.markDirty();
        try {
            getProxy().getTick()
                .alertDevice(getProxy().getNode());
        } catch (GridAccessException e) {

        }
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return inventory.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return inventory.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setInventorySlotContents(index, stack);
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        inventory.openInventory();
    }

    @Override
    public void closeInventory() {
        inventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemEssentiaPacket;
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return false;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return true;
    }

    @Override
    public void setSuction(Aspect aspect, int suction) {

    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        ItemStack packet = this.inventory.getStackInSlot(0);
        return packet != null ? ItemEssentiaPacket.getAspect(packet) : null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        ItemStack packet = this.inventory.getStackInSlot(0);
        if (packet != null) {
            Aspect packetAspect = ItemEssentiaPacket.getAspect(packet);
            long packetAmount = ItemEssentiaPacket.getEssentiaAmount(packet);

            if (packetAspect != null && packetAspect == aspect) {
                int take = (int) Math.min((long) amount, packetAmount);

                packetAmount -= take;

                if (packetAmount <= 0) {
                    this.inventory.setInventorySlotContents(0, null);
                } else {
                    this.inventory.setInventorySlotContents(0, ItemEssentiaPacket.newStack(packetAspect, packetAmount));
                }
                this.markDirty();
                this.markForUpdate();
                return take;
            }
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        ItemStack packet = this.inventory.getStackInSlot(0);
        return packet != null ? ItemEssentiaPacket.getAspect(packet) : null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        ItemStack packet = this.inventory.getStackInSlot(0);
        if (packet != null) {
            long amount = ItemEssentiaPacket.getEssentiaAmount(packet);
            return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
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

    @Override
    public AspectList getAspects() {
        ItemStack packet = this.inventory.getStackInSlot(0);
        if (packet != null) {
            Aspect aspect = ItemEssentiaPacket.getAspect(packet);
            long amount = ItemEssentiaPacket.getEssentiaAmount(packet);
            if (aspect != null && amount > 0) {
                return new AspectList().add(aspect, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount);
            }
        }
        return new AspectList();

    }

    @Override
    public void setAspects(AspectList aspects) {

    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return this.takeEssentia(tag, amount, ForgeDirection.UNKNOWN) == amount;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return this.getEssentiaAmount(ForgeDirection.UNKNOWN) >= amount
            && this.getEssentiaType(ForgeDirection.UNKNOWN) == tag;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect aspect : ot.getAspects()) {
            if (this.getEssentiaAmount(ForgeDirection.UNKNOWN) > 0
                && this.getEssentiaType(ForgeDirection.UNKNOWN) == aspect) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return this.getEssentiaType(ForgeDirection.UNKNOWN) == tag ? this.getEssentiaAmount(ForgeDirection.UNKNOWN) : 0;
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public void writeToStream(ByteBuf data) throws IOException {
        ItemStack packet = this.inventory.getStackInSlot(0);
        ByteBufUtils.writeItemStack(data, packet);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFromStream(ByteBuf data) throws IOException {
        ItemStack newStack = ByteBufUtils.readItemStack(data);
        this.inventory.setInventorySlotContents(0, newStack);
        return true;
    }
}
