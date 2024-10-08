package fqf.qua_mario.mixin;

import fqf.qua_mario.ModMarioQuaMarioClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

	// good lord how do i do this AAAAA
//	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;)"), method = "getLyingAngle", cancellable = true)
//	private void getLyingAngle(T entity, CallbackInfoReturnable<Float> cir) {
//		cir.setReturnValue(15.0F);
//	}

	@Shadow protected abstract float getLyingAngle(T entity);

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getLyingAngle(Lnet/minecraft/entity/LivingEntity;)F"), method = "setupTransforms", cancellable = true)
	private void squashedDeathAnimation(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale, CallbackInfo ci) {
		if(ModMarioQuaMarioClient.SQUASHED_ENTITIES.contains(entity)) {
			matrices.scale(1.4F, 0.25F, 1.4F);
			ci.cancel();
		}
	}
}
