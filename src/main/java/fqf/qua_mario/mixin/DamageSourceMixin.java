package fqf.qua_mario.mixin;

import fqf.qua_mario.ModQuakeMovement;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

	@Shadow @Final private @Nullable Entity attacker;

	@Shadow @Final private @Nullable Entity source;

	@Shadow public abstract String getName();

	@Shadow public abstract DamageType getType();

	@Inject(at = @At("HEAD"), method = "getDeathMessage", cancellable = true)
	protected void getDeathMessage(LivingEntity killed, CallbackInfoReturnable<Text> ci) {

		ModQuakeMovement.LOGGER.info("Damage type: " + getName());

		String name = getType().msgId();

		boolean isStomp = Objects.equals(name, "marioStomp");
		boolean isGroundPound = Objects.equals(name, "marioHipDrop");

		if((isStomp || isGroundPound) && attacker instanceof LivingEntity livingEntity) {
			ItemStack itemStack = livingEntity.getEquippedStack(isGroundPound ? EquipmentSlot.LEGS : EquipmentSlot.FEET);

			if(!itemStack.isEmpty() && itemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
				ci.setReturnValue(Text.translatable(
						"death.attack." + name + ".item",
						killed.getDisplayName(),
						attacker.getDisplayName(),
						itemStack.toHoverableText()
				));
			}
			else {
				ci.setReturnValue(Text.translatable(
						"death.attack." + name,
						killed.getDisplayName(),
						attacker.getDisplayName()
				));
			}
		}
	}
}
