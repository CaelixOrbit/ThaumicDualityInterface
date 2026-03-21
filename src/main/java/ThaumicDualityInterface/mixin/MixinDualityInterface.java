package ThaumicDualityInterface.mixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ThaumicDualityInterface.common.item.ItemEssentiaPacket;
import ThaumicDualityInterface.inventory.IDualEssentiaHost;
import ThaumicDualityInterface.util.DualityEssentiaInterface;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.util.inv.MEInventoryCrafting;
import thaumicenergistics.common.storage.AEEssentiaStack;

@Mixin(value = DualityInterface.class, remap = false)
public abstract class MixinDualityInterface {

    @Shadow
    @Final
    private IInterfaceHost iHost;

    private AEEssentiaStack getEssentiaFromStack(IAEStack<?> stack) {
        if (stack instanceof AEEssentiaStack) {
            return (AEEssentiaStack) stack;
        }
        if (stack instanceof IAEItemStack) {
            ItemStack is = ((IAEItemStack) stack).getItemStack();
            if (is != null && is.getItem() instanceof ItemEssentiaPacket) {
                return ItemEssentiaPacket.getEssentiaAEStack(is);
            }
        }
        return null;
    }

    @Inject(method = "pushPattern", at = @At("HEAD"), cancellable = true, remap = false)
    public void onPushPatternHead(ICraftingPatternDetails patternDetails, InventoryCrafting table,
        CallbackInfoReturnable<Boolean> cir) {
        if (!(this.iHost instanceof IDualEssentiaHost)) return;
        List<AEEssentiaStack> extractedEssentia = new ArrayList<>();
        boolean containsItemsOrFluids = false;
        for (int i = 0; i < table.getSizeInventory(); i++) {
            IAEStack<?> stack = ((MEInventoryCrafting) table).getAEStackInSlot(i);
            if (stack == null) continue;
            AEEssentiaStack essentia = getEssentiaFromStack(stack);
            if (essentia != null) {
                extractedEssentia.add(essentia);
            } else {
                containsItemsOrFluids = true;
            }
        }
        if (!extractedEssentia.isEmpty()) {
            if (!containsItemsOrFluids) {
                DualityEssentiaInterface essentiaBrain = ((IDualEssentiaHost) this.iHost).getDualityEssentia();
                for (AEEssentiaStack e : extractedEssentia) essentiaBrain.injectIntoCraftingBuffer(e.copy());
                essentiaBrain.saveChanges();
                cir.setReturnValue(true);
            }
        }
    }

    @Redirect(
        method = "pushPattern",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/util/inv/MEInventoryCrafting;getAEStackInSlot(I)Lappeng/api/storage/data/IAEStack;"),
        remap = false)
    public IAEStack<?> redirectGetAEStack(MEInventoryCrafting instance, int slot) {
        IAEStack<?> stack = instance.getAEStackInSlot(slot);
        if (this.iHost instanceof IDualEssentiaHost) {
            if (getEssentiaFromStack(stack) != null) {
                return null;
            }
        }
        return stack;
    }

    @Inject(method = "pushPattern", at = @At("RETURN"), remap = false)
    public void onPushPatternReturn(ICraftingPatternDetails patternDetails, InventoryCrafting table,
        CallbackInfoReturnable<Boolean> cir) {
        if (!(this.iHost instanceof IDualEssentiaHost)) return;
        boolean isSuccess = cir.getReturnValue();
        if (isSuccess) {
            List<AEEssentiaStack> extractedEssentia = new ArrayList<>();
            boolean containsItemsOrFluids = false;
            for (int i = 0; i < table.getSizeInventory(); i++) {
                IAEStack<?> stack = ((MEInventoryCrafting) table).getAEStackInSlot(i);
                if (stack == null) continue;
                AEEssentiaStack essentia = getEssentiaFromStack(stack);
                if (essentia != null) {
                    extractedEssentia.add(essentia);
                } else {
                    containsItemsOrFluids = true;
                }
            }
            if (!extractedEssentia.isEmpty() && containsItemsOrFluids) {
                DualityEssentiaInterface essentiaBrain = ((IDualEssentiaHost) this.iHost).getDualityEssentia();
                for (AEEssentiaStack e : extractedEssentia) essentiaBrain.injectIntoCraftingBuffer(e.copy());
                essentiaBrain.saveChanges();
            }
        }
    }
}
