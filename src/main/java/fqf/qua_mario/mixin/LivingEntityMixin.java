package fqf.qua_mario.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fqf.qua_mario.ModMarioQuaMario;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	// When the jump method checks if the player is sprinting, that should return false for Mario so that he doesn't get the bunnyhop boost.
	// Why is the boost applied in LivingEntity.jump instead of PlayerEntity.jump? I have no idea.
	@WrapOperation(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z", ordinal = 0))
	public boolean jump(LivingEntity instance, Operation<Boolean> original) {
		if(instance instanceof PlayerEntity player && ModMarioQuaMario.useMarioPhysics(player, false)) {
			return false;
		}
		return original.call(instance);
	}
}
