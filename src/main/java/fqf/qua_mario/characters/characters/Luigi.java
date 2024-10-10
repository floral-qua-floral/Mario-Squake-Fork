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
		this.statFactors.put(CharaStat.ALL_FRICTIONS, 0.7);
		this.statFactors.put(CharaStat.WALK_SPEED, 1.4);
		this.statFactors.put(CharaStat.RUN_SPEED, 1.2);
		this.statFactors.put(CharaStat.P_SPEED, 1.22);
		this.statFactors.put(CharaStat.ALL_JUMP_VELOCITIES, 1.15);
	}
}
