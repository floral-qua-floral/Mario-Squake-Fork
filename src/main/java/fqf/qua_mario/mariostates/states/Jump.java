package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

public class Jump extends MarioState {
	public static final Jump INSTANCE = new Jump();

	private Jump() {
		this.name = "Jump";

		preTickTransitions = CommonTransitions.PRE_TICK_JUMP_TRANSITIONS;
	}

	@Override
	public void tick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
		capJumpAndApplyGravity(CharaStat.JUMP_CAP);



		if(MarioClient.yVel < 0) StompBasic.INSTANCE.attemptStomp();
	}
}
