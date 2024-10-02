package fqf.qua_mario.mixin;

import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.SuperForm;
import fqf.qua_mario.util.MarioDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(Entity.class)
public abstract class EntityDataSaverMixin implements MarioDataSaver {
	@Unique private NbtCompound persistentData;
	@Unique private MarioCharacter character;
	@Unique private PowerUp powerUp;
	@Unique private final String MOD_DATA_NAME = ModMarioQuaMario.MOD_ID + ".data";

	@Override
	public NbtCompound marioQuaMario$getPersistentData() {
		if(this.persistentData == null)
			this.persistentData = new NbtCompound();

		return persistentData;
	}

	@Override
	public MarioCharacter marioQuaMario$getCharacter() {
		if(this.character == null)
			this.character = CharaMario.INSTANCE;
		return character;
	}

	@Override
	public void marioQuaMario$setCharacter(MarioCharacter newCharacter) {
		this.character = newCharacter == null ? CharaMario.INSTANCE : newCharacter;
	}

	@Override
	public PowerUp marioQuaMario$getPowerUp() {
		if(this.powerUp == null)
			this.powerUp = SuperForm.INSTANCE;
		return powerUp;
	}

	@Override
	public void marioQuaMario$setPowerUp(PowerUp newPowerUp) {
		this.powerUp = newPowerUp == null ? SuperForm.INSTANCE : newPowerUp;
	}


	@Inject(method = "writeNbt", at = @At("HEAD"))
	protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
		if(persistentData != null) {
			persistentData.putString("Character", character.getID().toString());
			persistentData.putString("PowerUp", powerUp.getID().toString());
			nbt.put(MOD_DATA_NAME, persistentData);
		}
	}

	@Inject(method = "readNbt", at = @At("HEAD"))
	protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
		if(nbt.contains(MOD_DATA_NAME, NbtElement.COMPOUND_TYPE)) {
			persistentData = nbt.getCompound(MOD_DATA_NAME);
			marioQuaMario$setCharacter(MarioRegistries.CHARACTERS.get(Identifier.of(persistentData.getString("Character"))));
			marioQuaMario$setPowerUp(MarioRegistries.POWER_UPS.get(Identifier.of(persistentData.getString("PowerUp"))));
		}
	}
}
