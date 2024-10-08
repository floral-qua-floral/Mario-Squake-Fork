package fqf.qua_mario.mixin;

import fqf.qua_mario.ModMarioQuaMario;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntitySwimPreventionMixin {
	@Inject(at = @At("HEAD"), method = "setSwimming(Z)V", cancellable = true)
	private void setSwimming(boolean swimming, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if(swimming && entity instanceof PlayerEntity playerized && ModMarioQuaMario.getIsMario(playerized)) {
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "isInSneakingPose", cancellable = true)
	private void isInSneakingPose(CallbackInfoReturnable<Boolean> cir) {
		if((Entity) (Object) this instanceof PlayerEntity player && ModMarioQuaMario.getSneakProhibited(player))
			cir.setReturnValue(false);
	}

	@Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
	private void setPose(EntityPose pose, CallbackInfo ci) {
		if(((Entity) (Object) this) instanceof PlayerEntity player && ModMarioQuaMario.getSneakProhibited(player) && pose == EntityPose.CROUCHING) {
			ci.cancel();
		}
	}

	@Inject(method = "getPose", at = @At("TAIL"), cancellable = true)
	private void getPose(CallbackInfoReturnable<EntityPose> cir) {
		if(((Entity) (Object) this) instanceof PlayerEntity player) {
			if(ModMarioQuaMario.getSneakProhibited(player) && cir.getReturnValue() == EntityPose.CROUCHING) {
				player.setPose(EntityPose.STANDING);
				cir.setReturnValue(EntityPose.STANDING);
			}
		}
	}
}
