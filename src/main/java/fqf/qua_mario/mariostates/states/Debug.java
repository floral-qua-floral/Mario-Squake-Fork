package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.mariostates.MarioState;

public class Debug extends MarioState {
	public static final Debug INSTANCE = new Debug();

	private Debug() {
		this.name = "Debug";

		postMoveTransitions = null;
	}

	@Override
	public void tick() {
		MarioClient.yVel = 0;

		MarioClient.approachAngleAndAccel(
				0.01, MarioClient.forwardInput * 0.5, MarioClient.forwardInput,
				0.01, MarioClient.rightwardInput * 0.5, MarioClient.rightwardInput,
				-1
		);

		if(Input.SPIN.isPressed()) MarioClient.changeState(Grounded.INSTANCE);

//		MarioClient.accelInfluence(
//				0.01, MarioClient.forwardInput * 0.55,
//				0.01, MarioClient.rightwardInput * 0.55,
//				0.1, 0.1
//		);

//		MarioClient.accelerate(
//				MarioClient.forwardInput * 0.5, MarioClient.rightwardInput * 0.5,
//				0.04, 0.04,
//				1, 1,
//				0.04, -0.04);
	}
}
