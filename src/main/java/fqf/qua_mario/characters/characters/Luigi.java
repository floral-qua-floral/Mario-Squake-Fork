package fqf.qua_mario.characters.characters;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.characters.MarioCharacter;
import net.minecraft.util.Identifier;

import java.util.EnumMap;

public class Luigi extends MarioCharacter {
	public static final Luigi INSTANCE = new Luigi();
	private Luigi() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "luigi");
		this.name = "Luigi";

		this.statFactors = new EnumMap<>(CharaStat.class);
		this.statFactors.put(CharaStat.WALK_SPEED, 2.0);
		this.statFactors.put(CharaStat.JUMP_VELOCITY, 1.25);
	}

	@Override
	public String getSoundPrefix() {
		return "luigi_";
	}
}
