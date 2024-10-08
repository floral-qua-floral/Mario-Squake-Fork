package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class DoubleJump extends AirborneState {
	public static final DoubleJump INSTANCE = new DoubleJump();

	private DoubleJump() {
		this.name = "Double Jump";
		this.isJump = true;
		this.jumpCapStat = CharaStat.DOUBLE_JUMP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
			() -> {
				MarioState landingResult = AirborneTransitions.LANDING.evaluate();
				if(landingResult != null) {
					MarioClient.doubleJumpLandingTime = 3;
					return landingResult;
				}
				return null;
			},
			AirborneTransitions.GROUND_POUND
		));
	}

	@Override
	protected void airTick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
	}
}
