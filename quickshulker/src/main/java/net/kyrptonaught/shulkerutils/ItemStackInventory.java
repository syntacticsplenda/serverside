package net.kyrptonaught.shulkerutils;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

// Vendored from shulkerutils for 26.3: an inventory backed by an ItemStack's
// container component (formerly the BlockEntity tag).
public class ItemStackInventory implements Container {
    private final ItemStack stack;
    private final NonNullList<ItemStack> items;

    public ItemStackInventory(ItemStack stack, int size) {
        this.stack = stack;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.readFromStack();
    }

    private void readFromStack() {
        ItemContainerContents contents = this.stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (contents.size() > 0) {
            NonNullList<ItemStack> stored = NonNullList.withSize(contents.size(), ItemStack.EMPTY);
            contents.copyInto(stored);
            for (int i = 0; i < this.items.size() && i < stored.size(); i++) {
                this.items.set(i, stored.get(i));
            }
        }
    }

    private void writeToStack() {
        this.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = this.items.get(slot).split(amount);
        this.setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        this.setChanged();
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        this.writeToStack();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
}
