package fqf.qua_mario.mariostates;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioSideflip extends MarioState {
	public static final MarioSideflip INSTANCE = new MarioSideflip();

	private MarioSideflip() {
		this.name = "Sideflip";

		preTickTransitions = new ArrayList<MarioStateTransition>(Arrays.asList(new MarioStateTransition[]{
				CommonTransitions.LANDING,
		}));
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
