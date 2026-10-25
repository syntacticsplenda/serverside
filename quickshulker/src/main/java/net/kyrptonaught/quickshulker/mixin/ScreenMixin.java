package net.kyrptonaught.quickshulker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.kyrptonaught.quickshulker.client.QuickShulkerModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import java.nio.DoubleBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin {
    @Shadow
    protected Slot hoveredSlot;

    @Shadow
    protected AbstractContainerMenu menu;

    @Inject(method = "init", at = @At("TAIL"))
    private void fixMouse(CallbackInfo ci) {
        if (QuickShulkerMod.lastMouseX != 0 && QuickShulkerMod.lastMouseY != 0) {
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), QuickShulkerMod.lastMouseX, QuickShulkerMod.lastMouseY);
            QuickShulkerMod.lastMouseY = 0;
            QuickShulkerMod.lastMouseX = 0;
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void QS$keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().keybingInInv) {
            if (QuickShulkerModClient.getKeyMapping().matches(keyEvent)) {
                long window = Minecraft.getInstance().getWindow().handle();
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    DoubleBuffer xPos = stack.mallocDouble(1);
                    DoubleBuffer yPos = stack.mallocDouble(1);
                    GLFW.glfwGetCursorPos(window, xPos, yPos);
                    if (handleTrigger(xPos.get(0), yPos.get(0)))
                        cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void QS$mousePressed(MouseButtonEvent mouseButtonEvent, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (QuickShulkerMod.getConfig().rightClickInv) {
            if (this.menu.getCarried().isEmpty() && mouseButtonEvent.button() == 1 && this.hoveredSlot != null && this.hoveredSlot.getItem().getCount() == 1) {
                if (handleTrigger(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        if (QuickShulkerMod.getConfig().keybingInInv) {
            if (QuickShulkerModClient.getKeyMapping().matchesMouse(mouseButtonEvent)) {
                if (handleTrigger(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }

    @Unique
    private boolean handleTrigger(double mouseX, double mouseY) {
        if (this.hoveredSlot != null) {
            return isValid(this.hoveredSlot.getItem(), ClientUtil.getSlotId(this.menu, this.hoveredSlot), mouseX, mouseY);
        }
        return false;
    }

    @Unique
    private boolean isValid(ItemStack stack, int id, double mouseX, double mouseY) {
        if (this.hoveredSlot.container instanceof Inventory)
            if (ClientUtil.CheckAndSend(stack, id)) {
                QuickShulkerMod.lastMouseX = mouseX;
                QuickShulkerMod.lastMouseY = mouseY;
                return true;
            }
        return false;
    }
}
