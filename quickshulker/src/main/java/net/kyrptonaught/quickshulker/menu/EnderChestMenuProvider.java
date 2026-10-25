package net.kyrptonaught.quickshulker.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;

public class EnderChestMenuProvider implements MenuProvider {
    private final Container enderChest;

    public EnderChestMenuProvider(Container enderChest) {
        this.enderChest = enderChest;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.enderchest");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return ChestMenu.threeRows(containerId, playerInventory, this.enderChest);
    }
}
