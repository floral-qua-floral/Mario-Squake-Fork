package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;

public class Sideflip extends MarioState {
	public static final Sideflip INSTANCE = new Sideflip();

	private Sideflip() {
		this.name = "Sideflip";

		preTickTransitions = CommonTransitions.PRE_TICK_JUMP_TRANSITIONS;
	}

	@Override
	public void tick() {
		MarioClient.stateTimer++;
		MarioClient.aerialAccel(MarioClient.stateTimer > 5 ? MarioClient.forwardInput * 0.04: 0, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);

		capJumpAndApplyGravity(CharaStat.SIDEFLIP_CAP);
	}
}
