package net.kyrptonaught.quickshulker.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record OpenInventoryPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenInventoryPacket> OPEN_INV = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(QuickShulkerMod.MOD_ID, "openinv"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInventoryPacket> STREAM_CODEC = StreamCodec.unit(new OpenInventoryPacket());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return OPEN_INV;
    }

    public static void registerPayload() {
        PayloadTypeRegistry.clientboundPlay().register(OPEN_INV, STREAM_CODEC);
    }

    public static void send(ServerPlayer player) {
        ServerPlayNetworking.send(player, new OpenInventoryPacket());
    }
}
