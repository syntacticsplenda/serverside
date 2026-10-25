package net.kyrptonaught.quickshulker.mixin;

import net.kyrptonaught.quickshulker.ItemInventoryContainer;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AbstractContainerMenu.class)
public abstract class ContainerMixin implements ItemInventoryContainer {

    int playerInvSlot = -1;

    public int getUsedSlotInPlayerInv() {
        return playerInvSlot;
    }

    public void setUsedSlot(int playerInvSlotID) {
        this.playerInvSlot = playerInvSlotID;
    }

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Shadow
    public abstract ItemStack getCarried();

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    public void QS$onClick(int slotId, int button, ContainerInput actionType, Player player, CallbackInfo ci) {
        if (slotId > 0 && slotId < slots.size()) {
            if (hasItem())
                if (slots.get(slotId).container instanceof net.minecraft.world.entity.player.Inventory && slots.get(slotId).getContainerSlot() == playerInvSlot)
                    ci.cancel();
        }
    }

    // Server-side right-click interception, following the Better Shulkers
    // paradigm: the server already receives every vanilla inventory click,
    // so right-clicking an openable item in the player inventory opens it
    // without needing anything on the client.
    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    public void QS$openOnRightClick(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        if (QuickShulkerMod.getConfig().rightClickInv
                && !player.level().isClientSide()
                && containerInput == ContainerInput.PICKUP
                && buttonNum == 1
                && slotIndex >= 0 && slotIndex < slots.size()) {
            Slot slot = slots.get(slotIndex);
            ItemStack stack = slot.getItem();
            if (this.getCarried().isEmpty()
                    && slot.container == player.getInventory()
                    && stack.getCount() == 1
                    && Util.isOpenableItem(stack)) {
                Util.openItem(player, slotIndex);
                ci.cancel();
            }
        }
    }
}
