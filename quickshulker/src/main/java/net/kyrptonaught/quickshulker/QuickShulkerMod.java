package net.kyrptonaught.quickshulker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.api.*;
import net.kyrptonaught.quickshulker.config.ConfigManager;
import net.kyrptonaught.quickshulker.config.ConfigOptions;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.network.OpenShulkerPacket;
import net.kyrptonaught.quickshulker.network.QuickBundlePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.kyrptonaught.shulkerutils.ItemStackInventory;
import net.minecraft.world.SimpleMenuProvider;

public class QuickShulkerMod implements ModInitializer, RegisterQuickShulker {
    public static final String MOD_ID = "quickshulker";
    public static final ConfigManager config = new ConfigManager(MOD_ID, new ConfigOptions());
    public static double lastMouseX, lastMouseY;

    @Override
    public void onInitialize() {
        config.load();
        OpenShulkerPacket.registerReceivePacket();
        QuickBundlePacket.registerReceivePacket();
        OpenInventoryPacket.registerPayload();
        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide()) {
                if (QuickShulkerMod.getConfig().rightClickToOpen) {
                    if (Util.isOpenableItem(stack) && Util.canOpenInHand(stack)) {
                        if (hand == InteractionHand.MAIN_HAND)
                            Util.openItem(player, 0, player.getInventory().getSelectedSlot());
                        else Util.openItem(player, 0, Inventory.SLOT_OFFHAND);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });
        FabricLoader.getInstance().getEntrypoints(MOD_ID, RegisterQuickShulker.class).forEach(RegisterQuickShulker::registerProviders);
    }

    public static ConfigOptions getConfig() {
        return (ConfigOptions) config.getConfig();
    }

    @Override
    public void registerProviders() {
        if (getConfig().quickShulkerBox)
            new QuickOpenableRegistry.Builder()
                    .setItem(ShulkerBoxBlock.class)
                    .supportsBundleing(true)
                    .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            new ShulkerBoxMenu(i, player.getInventory(), new ItemStackInventory(stack, 27)), stack.getCustomName() != null ? stack.getHoverName() : Component.translatable("container.shulkerBox")))))
                    .register();

        if (getConfig().quickEChest)
            new QuickOpenableRegistry.Builder(new QuickShulkerData.QuickEnderData())
                    .setItem(EnderChestBlock.class)
                    .supportsBundleing(true)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            ChestMenu.threeRows(i, playerInventory, player.getEnderChestInventory()), Component.translatable("container.enderchest")))))
                    .register();

        if (getConfig().quickCraftingTables)
            new QuickOpenableRegistry.Builder()
                    .setItem(CraftingTableBlock.class)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            new CraftingMenu(i, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), Component.translatable("container.crafting")))))
                    .register();

        if (getConfig().quickStonecutter)
            new QuickOpenableRegistry.Builder()
                    .setItem(StonecutterBlock.class)
                    .ignoreSingleStackCheck(true)
                    .setOpenAction(((player, stack) -> player.openMenu(new SimpleMenuProvider((i, playerInventory, playerEntity) ->
                            new StonecutterMenu(i, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), Component.translatable("container.stonecutter")))))
                    .register();
    }
}
