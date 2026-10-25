package com.turtlearmymc.blastproofnetherite.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    // Covers the void in a collidable VoxelShape for fireproof items. This hooks
    // the call site of collideBoundingBox in collide() and passes the extra shape
    // through the entity colliders argument, rather than redirecting inside
    // collideBoundingBox itself, because collision-optimizing mods (e.g. Lithium)
    // replace collideBoundingBox's body and would break that injection.
    @WrapOperation(method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 coverTheVoidForFireproofItems(Entity source, Vec3 movement, AABB collisionBox, Level level,
            List<VoxelShape> entityColliders, Operation<Vec3> original) {
        if (source instanceof ItemEntity item && !item.getItem().isEmpty() && item.fireImmune()) {
            List<VoxelShape> colliders = new ArrayList<>(entityColliders);
            colliders.add(Shapes.box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, level.getMinY(),
                    Double.POSITIVE_INFINITY));
            return original.call(source, movement, collisionBox, level, colliders);
        }
        return original.call(source, movement, collisionBox, level, entityColliders);
    }
}
