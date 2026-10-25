package net.kyrptonaught.quickshulker.menu;

import net.kyrptonaught.shulkerutils.ItemStackInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;

// Menu initialization follows the Better Shulkers paradigm: the container
// itself is the MenuProvider, capturing its display name at construction and
// building the menu in createMenu instead of an inline SimpleMenuProvider.
public class QuickShulkerBoxContainer extends ItemStackInventory implements MenuProvider {
    private final Component displayName;

    public QuickShulkerBoxContainer(ItemStack stack, int size) {
        super(stack, size);
        this.displayName = stack.getDisplayName();
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ShulkerBoxMenu(containerId, playerInventory, this);
    }
}
