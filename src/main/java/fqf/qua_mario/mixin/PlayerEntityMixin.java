package fqf.qua_mario.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModQuakeMovement;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("HEAD"), method = "travel(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
	private void travel(Vec3d movementInput, CallbackInfo ci) {
		if (!ModQuakeMovement.CONFIG.isEnabled())
			return;

		if (MarioClient.attempt_travel((PlayerEntity) (Object) this, movementInput))
			ci.cancel();
	}
}
