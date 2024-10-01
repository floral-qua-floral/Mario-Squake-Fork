package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;

public class Sideflip extends MarioState {
	public static final Sideflip INSTANCE = new Sideflip();

	private Sideflip() {
		this.name = "Sideflip";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				CommonTransitions.LANDING
		));
	}

	@Override
	public void tick() {
		MarioClient.stateTimer++;
		MarioClient.aerialAccel(MarioClient.stateTimer > 5 ? MarioClient.forwardInput * 0.04: 0, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
		applyGravity();

		final double CAP_SPEED = 0.4;
		if((MarioInputs.isHeld(MarioInputs.Key.SNEAK) || !MarioInputs.isHeld(MarioInputs.Key.JUMP)) && MarioClient.yVel > CAP_SPEED) {
			MarioClient.yVel = CAP_SPEED;
		}
	}
}
