package net.kyrptonaught.quickshulker.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingTableMenuProvider implements MenuProvider {
    private final ContainerLevelAccess access;

    public CraftingTableMenuProvider(Player player) {
        this.access = ContainerLevelAccess.create(player.level(), player.blockPosition());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crafting");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CraftingMenu(containerId, playerInventory, this.access);
    }
}
