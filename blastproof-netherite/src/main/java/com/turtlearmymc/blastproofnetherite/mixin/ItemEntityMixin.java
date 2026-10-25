package com.turtlearmymc.blastproofnetherite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    // Makes fireproof items invulnerable to damage
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void blastproof$invulnerableToDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> ci) {
        cancelNonVoidDamage(source, ci);
    }

    @Inject(method = "hurtClient", at = @At("HEAD"), cancellable = true)
    private void blastproof$invulnerableToDamageClient(DamageSource source, CallbackInfoReturnable<Boolean> ci) {
        cancelNonVoidDamage(source, ci);
    }

    private void cancelNonVoidDamage(DamageSource source, CallbackInfoReturnable<Boolean> ci) {
        ItemEntity item = (ItemEntity) (Object) this;
        if (!item.getItem().isEmpty() && item.fireImmune() && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            ci.setReturnValue(false);
        }
    }

    // Teleports fireproof items out of the void to prevent them from being
    // deleted at the start of their tick.
    @Inject(method = "tick()V", at = @At("HEAD"))
    protected void tick(CallbackInfo ci) {
        ItemEntity item = (ItemEntity) (Object) this;
        if (item.fireImmune() && item.getY() < item.level().getMinY()) {
            item.setBoundingBox(item.getBoundingBox().move(0, item.level().getMinY() - item.getY(), 0));
            item.setPos(item.getX(), item.getBoundingBox().min(Direction.Axis.Y), item.getZ());
            item.setDeltaMovement(item.getDeltaMovement().multiply(1, 0, 1));
        }
    }
}
