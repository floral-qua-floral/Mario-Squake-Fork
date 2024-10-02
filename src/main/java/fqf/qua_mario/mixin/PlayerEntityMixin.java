package fqf.qua_mario.mixin;

import fqf.qua_mario.ModMarioQuaMario;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fqf.qua_mario.MarioClient;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("HEAD"), method = "travel(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true)
	private void travel(Vec3d movementInput, CallbackInfo ci) {
		if (MarioClient.attemptMarioTravel((PlayerEntity) (Object) this, movementInput))
			ci.cancel();
	}

	@Inject(at = @At("TAIL"), method = "jump()V")
	private void afterJump(CallbackInfo info) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if(ModMarioQuaMario.useMarioPhysics(player, false) && player.isSprinting()) {
			float bunnyhopSpeedBonus = (float) Math.toRadians(player.getYaw());
			Vec3d deltaVelocity = new Vec3d(MathHelper.sin(bunnyhopSpeedBonus) * 0.2F, 0, -(MathHelper.cos(bunnyhopSpeedBonus) * 0.2F));
			player.setVelocity(player.getVelocity().add(deltaVelocity));
		}
	}
}