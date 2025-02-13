package fqf.qua_mario.powerups.forms;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.OldMarioState;
import fqf.qua_mario.powerups.StatChangingPowerUp;
import net.minecraft.util.Identifier;

import java.util.EnumMap;

public class MiniForm extends StatChangingPowerUp {
	public static final MiniForm INSTANCE = new MiniForm();
	private MiniForm() {
		this.ID = Identifier.of(ModMarioQuaMario.MOD_ID, "mini_form");
		this.prefix = "";
		this.widthFactor = 0.25F;
		this.heightFactor = 0.125F;
		this.voicePitch = 1.5F;

		this.statFactors = new EnumMap<>(CharaStat.class);
		this.statFactors.put(CharaStat.ALL_JUMP_VELOCITIES, 0.75);
		this.statFactors.put(CharaStat.ALL_GRAVITIES, 0.375);
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
