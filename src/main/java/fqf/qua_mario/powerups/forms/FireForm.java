package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

public class FireForm extends PowerUp {
	public static final FireForm INSTANCE = new FireForm();
	private FireForm() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "fire_form");
		this.prefix = "Fire ";
		this.widthFactor = 1.0F;
		this.heightFactor = 1.0F;
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
