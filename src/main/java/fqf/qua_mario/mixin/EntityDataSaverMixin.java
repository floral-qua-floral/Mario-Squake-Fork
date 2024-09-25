package fqf.qua_mario.mixin;

import fqf.qua_mario.ModQuakeMovement;
import fqf.qua_mario.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDataSaverMixin implements IEntityDataSaver {
	private NbtCompound persistentData;
	private final String MOD_DATA_NAME = ModQuakeMovement.MOD_ID + ".data";

	@Override
	public NbtCompound getPersistentData() {
		if(this.persistentData == null)
			this.persistentData = new NbtCompound();

		return persistentData;
	}

	@Inject(method = "writeNbt", at = @At("HEAD"))
	protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
		if(persistentData != null) {
			nbt.put(MOD_DATA_NAME, persistentData);
		}
	}

	@Inject(method = "readNbt", at = @At("HEAD"))
	protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
		if(nbt.contains(MOD_DATA_NAME, NbtElement.COMPOUND_TYPE)) {
			persistentData = nbt.getCompound(MOD_DATA_NAME);
		}
	}
}
