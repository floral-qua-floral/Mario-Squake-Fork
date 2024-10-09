package fqf.qua_mario.mixin;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.ModMarioQuaMarioClient;
import fqf.qua_mario.stomptypes.StompHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	// Code stolen from FlatteringAnvils by ItsFelix5
	@Redirect(method = "onEntityDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onDamaged(Lnet/minecraft/entity/damage/DamageSource;)V"))
	private void onDamaged(Entity instance, DamageSource damageSource) {
		ModMarioQuaMario.LOGGER.info("onEntityDamage mixin! ");
		if(instance instanceof LivingEntity livingInstance) ModMarioQuaMario.LOGGER.info("Dead: " + livingInstance.isDead());
		if(damageSource.isIn(StompHandler.FLATTENS_ENTITIES_TAG)) {
			ModMarioQuaMario.LOGGER.info("Added to Squashed Entities!");
			ModMarioQuaMarioClient.SQUASHED_ENTITIES.add(instance);
		}
		instance.onDamaged(damageSource);
	}

	
}
