package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;

public class Aerial extends MarioState {
	public static final Aerial INSTANCE = new Aerial();

	private Aerial() {
		this.name = "Aerial";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				CommonTransitions.LANDING
		));
	}

	@Override
	public void tick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
		applyGravity();
	}
}
