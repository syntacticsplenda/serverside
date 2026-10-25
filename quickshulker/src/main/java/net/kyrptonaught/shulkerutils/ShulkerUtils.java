package net.kyrptonaught.shulkerutils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

// Vendored from shulkerutils for 26.3.
public class ShulkerUtils {
    public static boolean isShulkerItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    public static ItemStackInventory getInventoryFromShulker(ItemStack stack) {
        return new ItemStackInventory(stack, 27);
    }
}
