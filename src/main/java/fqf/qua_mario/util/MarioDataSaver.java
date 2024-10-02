package fqf.qua_mario.util;

import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.nbt.NbtCompound;

public interface MarioDataSaver {
	NbtCompound marioQuaMario$getPersistentData();
	MarioCharacter marioQuaMario$getCharacter();
	void marioQuaMario$setCharacter(MarioCharacter newCharacter);
	PowerUp marioQuaMario$getPowerUp();
	void marioQuaMario$setPowerUp(PowerUp newPowerUp);
}