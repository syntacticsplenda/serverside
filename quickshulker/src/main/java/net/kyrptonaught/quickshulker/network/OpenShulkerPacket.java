package net.kyrptonaught.quickshulker.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenShulkerPacket(int invSlot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenShulkerPacket> OPEN_SHULKER_PACKET = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "open_shulker_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenShulkerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenShulkerPacket::invSlot,
            OpenShulkerPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return OPEN_SHULKER_PACKET;
    }

    public static void registerReceivePacket() {
        PayloadTypeRegistry.serverboundPlay().register(OPEN_SHULKER_PACKET, STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OPEN_SHULKER_PACKET, (payload, context) -> {
            int invSlot = payload.invSlot();
            context.server().execute(() -> Util.openItem(context.player(), invSlot));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendOpenPacket(int invSlot) {
        ClientPlayNetworking.send(new OpenShulkerPacket(invSlot));
    }
}
