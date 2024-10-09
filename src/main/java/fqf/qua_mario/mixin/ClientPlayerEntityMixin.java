package fqf.qua_mario.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fqf.qua_mario.ModMarioQuaMario;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Inject(at = @At("HEAD"), method = "shouldSlowDown", cancellable = true)
	private void shouldSlowDown(CallbackInfoReturnable<Boolean> cir) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		if(ModMarioQuaMario.useMarioPhysics(player, true) && !player.isCrawling()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "isInSneakingPose", cancellable = true)
	private void isInSneakingPose(CallbackInfoReturnable<Boolean> cir) {
		if(ModMarioQuaMario.getSneakProhibited((PlayerEntity) (Object) this)) cir.setReturnValue(false);
	}

//	@Inject(at = @At("HEAD"), method = "jump")

//	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldSwimInFluids()Z"), method = "tickMovement")
//	private boolean boogle(ClientPlayerEntity instance, Operation<Boolean> original) {
//
//		return false;
//	}
}
