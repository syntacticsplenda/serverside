package net.kyrptonaught.quickshulker.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Shadow
    public ServerGamePacketListenerImpl connection;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    // Following the Better Shulkers paradigm: resync the client's cursor
    // prediction before opening a menu, so a vanilla client that predicted
    // picking up the clicked item doesn't ghost it onto the cursor.
    @Inject(method = "openMenu", at = @At("HEAD"))
    private void QS$syncCursor(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir) {
        this.connection.send(new ClientboundSetCursorItemPacket(this.containerMenu.getCarried()));
    }
}
