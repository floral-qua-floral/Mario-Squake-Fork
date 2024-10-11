package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.oldmariostates.OldMarioState;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.util.Identifier;

public class SmallForm extends PowerUp {
	public static final SmallForm INSTANCE = new SmallForm();
	private SmallForm() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "small_form");
		this.prefix = "";
		this.widthFactor = 1.0F;
		this.heightFactor = 0.5F;
		this.voicePitch = 1.075F;
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
