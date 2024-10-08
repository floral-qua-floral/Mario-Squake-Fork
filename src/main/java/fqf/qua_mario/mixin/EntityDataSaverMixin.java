package fqf.qua_mario.mixin;

import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.forms.SuperForm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(Entity.class)
public abstract class EntityDataSaverMixin {
	@Unique private final String MOD_DATA_NAME = ModMarioQuaMario.MOD_ID + ".data";


	@Inject(method = "writeNbt", at = @At("HEAD"))
	protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
		NbtCompound persistentData = new NbtCompound();

		Entity entity = (Entity) (Object) this;
		if(entity instanceof PlayerEntity player) {
			persistentData.putBoolean("isMario", ModMarioQuaMario.getIsMario(player));
			persistentData.putString("Character", ModMarioQuaMario.getCharacter(player).getID().toString());
			persistentData.putString("PowerUp", ModMarioQuaMario.getPowerUp(player).getID().toString());
			nbt.put(MOD_DATA_NAME, persistentData);
		}
	}

	@Inject(method = "readNbt", at = @At("HEAD"))
	protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
		Entity entity = (Entity) (Object) this;
		if(entity instanceof ServerPlayerEntity serverPlayer) {
			if(nbt.contains(MOD_DATA_NAME, NbtElement.COMPOUND_TYPE)) {
				NbtCompound persistentData = nbt.getCompound(MOD_DATA_NAME);
				ModMarioQuaMario.setFullMarioData(
						serverPlayer,
						persistentData.getBoolean("isMario"),
						MarioRegistries.CHARACTERS.get(Identifier.of(persistentData.getString("Character"))),
						MarioRegistries.POWER_UPS.get(Identifier.of(persistentData.getString("PowerUp")))
				);
			}
			else {
				ModMarioQuaMario.setFullMarioData(
						serverPlayer,
						true,
						CharaMario.INSTANCE,
						SuperForm.INSTANCE
				);
			}
		}
	}
}
