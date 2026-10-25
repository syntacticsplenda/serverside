package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.BundleHelper;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class QuickBundlePacket {
    public static void registerReceivePacket() {
        Bundle.registerReceivePacket();
        BundleIntoHeld.registerReceivePacket();
        Unbundle.registerReceivePacket();
    }

    public static void sendCreativeSlotUpdate(ItemStack output, Slot slot) {
        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(output, slot.index);
    }

    public record Bundle(int playerInvSlotID, ItemStack stackToBundle) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<Bundle> QUICK_BUNDLE_PACKET = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundle_packet"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Bundle> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Bundle::playerInvSlotID,
                ItemStack.OPTIONAL_STREAM_CODEC, Bundle::stackToBundle,
                Bundle::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return QUICK_BUNDLE_PACKET;
        }

        public static void registerReceivePacket() {
            PayloadTypeRegistry.serverboundPlay().register(QUICK_BUNDLE_PACKET, STREAM_CODEC);
            ServerPlayNetworking.registerGlobalReceiver(QUICK_BUNDLE_PACKET, (payload, context) -> {
                if (context.player().isCreative()) {
                    int playerInvSlotID = payload.playerInvSlotID();
                    ItemStack stackToBundle = payload.stackToBundle();
                    context.server().execute(() -> BundleHelper.bundleItemIntoStack(context.player(), context.player().getInventory().getItem(playerInvSlotID), stackToBundle, null));
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(int slotID, ItemStack stackToBundle) {
            ClientPlayNetworking.send(new Bundle(slotID, stackToBundle));
        }
    }

    public record BundleIntoHeld(ItemStack stackToBundle, ItemStack bundleStack) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<BundleIntoHeld> QUICK_BUNDLEHELD_PACKET = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_bundleheld_packet"));
        public static final StreamCodec<RegistryFriendlyByteBuf, BundleIntoHeld> STREAM_CODEC = StreamCodec.composite(
                ItemStack.OPTIONAL_STREAM_CODEC, BundleIntoHeld::stackToBundle,
                ItemStack.OPTIONAL_STREAM_CODEC, BundleIntoHeld::bundleStack,
                BundleIntoHeld::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return QUICK_BUNDLEHELD_PACKET;
        }

        public static void registerReceivePacket() {
            PayloadTypeRegistry.serverboundPlay().register(QUICK_BUNDLEHELD_PACKET, STREAM_CODEC);
            ServerPlayNetworking.registerGlobalReceiver(QUICK_BUNDLEHELD_PACKET, (payload, context) -> {
                if (context.player().isCreative()) {
                    ItemStack stackToBundle = payload.stackToBundle();
                    ItemStack bundleStack = payload.bundleStack();
                    context.server().execute(() -> BundleHelper.bundleItemIntoStack(context.player(), bundleStack, stackToBundle, null));
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(ItemStack stackToBundle, ItemStack bundleStack) {
            ClientPlayNetworking.send(new BundleIntoHeld(stackToBundle, bundleStack));
        }
    }

    public record Unbundle(int playerInvSlotID, ItemStack unbundleStack) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<Unbundle> QUICK_UNBUNDLE_PACKET = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "quick_unbundle_packet"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Unbundle> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Unbundle::playerInvSlotID,
                ItemStack.OPTIONAL_STREAM_CODEC, Unbundle::unbundleStack,
                Unbundle::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return QUICK_UNBUNDLE_PACKET;
        }

        public static void registerReceivePacket() {
            PayloadTypeRegistry.serverboundPlay().register(QUICK_UNBUNDLE_PACKET, STREAM_CODEC);
            ServerPlayNetworking.registerGlobalReceiver(QUICK_UNBUNDLE_PACKET, (payload, context) -> {
                if (context.player().isCreative()) {
                    int playerInvSlotID = payload.playerInvSlotID();
                    ItemStack unbundleStack = payload.unbundleStack();
                    context.server().execute(() -> {
                        ItemStack output = BundleHelper.unbundleItem(context.player(), unbundleStack);
                        if (output != null)
                            context.player().getInventory().setItem(playerInvSlotID, output);
                    });
                }
            });
        }

        @Environment(EnvType.CLIENT)
        public static void sendPacket(int slotID, ItemStack unbundleStack) {
            ClientPlayNetworking.send(new Unbundle(slotID, unbundleStack));
        }
    }
}
