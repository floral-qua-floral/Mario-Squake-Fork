package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

import java.util.EnumMap;

public class SmallForm extends PowerUp {
	public static final SmallForm INSTANCE = new SmallForm();
	private SmallForm() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "small_form");
		this.prefix = "";
		this.widthFactor = 1.0F;
		this.heightFactor = 0.5F;
	}

	@Override
	public MarioState customTransition(MarioState state, MarioState.TransitionPhases phase) {
		return null;
	}

	@Override
	public MarioState interceptTransition(MarioState from, MarioState to) {
		return null;
	}
}
