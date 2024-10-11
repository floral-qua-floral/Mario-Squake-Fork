package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.oldmariostates.OldMarioState;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

public class FireForm extends PowerUp {
	public static final FireForm INSTANCE = new FireForm();
	private FireForm() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "fire_form");
		this.prefix = "Fire ";
	}

	@Override
	public OldMarioState customTransition(OldMarioState state, OldMarioState.TransitionPhases phase) {
		return null;
	}

	@Override
	public OldMarioState interceptTransition(OldMarioState from, OldMarioState to) {
		return null;
	}
}
