package fqf.qua_mario.mariostates;

import fqf.qua_mario.MarioClient;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioDebug extends MarioState {
	public static final MarioDebug INSTANCE = new MarioDebug();

	private MarioDebug() {
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
