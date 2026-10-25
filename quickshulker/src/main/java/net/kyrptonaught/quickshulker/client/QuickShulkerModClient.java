package net.kyrptonaught.quickshulker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulkerClient;
import net.kyrptonaught.quickshulker.network.OpenInventoryPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class QuickShulkerModClient implements ClientModInitializer {

    public static final KeyMapping OPEN_ITEM_KEY = new KeyMapping("key.quickshulker.quickshulker",
            GLFW.GLFW_KEY_K, KeyMapping.Category.register(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quickshulker")));

    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(OPEN_ITEM_KEY);
        ClientTickEvents.START_LEVEL_TICK.register(clientLevel -> {
            Minecraft client = Minecraft.getInstance();
            if (client.gui.screen() == null && QuickShulkerMod.getConfig().keybind) {
                LocalPlayer player = client.player;
                if (OPEN_ITEM_KEY.isDown() && player != null) {
                    if (player.getMainHandItem().isEmpty() && !player.getOffhandItem().isEmpty())
                        ClientUtil.CheckAndSend(player.getOffhandItem(), 45);
                    else
                        ClientUtil.CheckAndSend(player.getMainHandItem(), 36 + player.getInventory().getSelectedSlot());
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.OPEN_INV, (payload, context) -> {
            context.client().execute(() -> {
                context.client().gui.setScreen(new InventoryScreen(context.client().player));
            });
        });
        FabricLoader.getInstance().getEntrypoints(QuickShulkerMod.MOD_ID + "_client", RegisterQuickShulkerClient.class).forEach(RegisterQuickShulkerClient::registerClient);
    }

    public static KeyMapping getKeyMapping() {
        return OPEN_ITEM_KEY;
    }
}
