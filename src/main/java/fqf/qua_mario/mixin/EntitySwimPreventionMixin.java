package fqf.qua_mario.mixin;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.util.MarioDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntitySwimPreventionMixin {
	@Inject(at = @At("HEAD"), method = "setSwimming(Z)V", cancellable = true)
	private void setSwimming(boolean swimming, CallbackInfo ci) {
		Entity entity = (Entity) (Object) this;
		if(swimming && entity instanceof PlayerEntity playerized) {


			if(playerized.getWorld().isClient) { // Client-side
				if(MarioClient.isMario)
					ci.cancel();
			}
			else { // Server-side
				if(((MarioDataSaver) playerized).marioQuaMario$getPersistentData().getBoolean("isMario"))
					ci.cancel();
			}
		}
	}
}
