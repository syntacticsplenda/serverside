package com.turtlearmymc.blastproofnetherite.mixin;

import com.google.common.collect.ImmutableList;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    private static List<VoxelShape> collectCollidersIgnoringWorldBorder(Entity source, Level level, List<VoxelShape> entityColliders, AABB boundingBox) {
        throw new AssertionError();
    }

    // Covers the void in a collidable VoxelShape for fireproof items
    @Redirect(method = "collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collectCollidersIgnoringWorldBorder(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private static List<VoxelShape> coverTheVoidForFireproofItems(Entity source, Level level, List<VoxelShape> entityColliders, AABB boundingBox) {
        List<VoxelShape> colliders = EntityMixin.collectCollidersIgnoringWorldBorder(source, level, entityColliders, boundingBox);
        if (source instanceof ItemEntity item && !item.getItem().isEmpty() && item.fireImmune()) {
            ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(colliders.size() + 1);
            builder.addAll(colliders);
            builder.add(Shapes.box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, level.getMinY(),
                    Double.POSITIVE_INFINITY));
            return builder.build();
        }
        return colliders;
    }
}
