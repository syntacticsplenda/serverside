package net.kyrptonaught.quickshulker.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;

public class StonecutterMenuProvider implements MenuProvider {
    private final ContainerLevelAccess access;

    public StonecutterMenuProvider(Player player) {
        this.access = ContainerLevelAccess.create(player.level(), player.blockPosition());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.stonecutter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new StonecutterMenu(containerId, playerInventory, this.access);
    }
}
