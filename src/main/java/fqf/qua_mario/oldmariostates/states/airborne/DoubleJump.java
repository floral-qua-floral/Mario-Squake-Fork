package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;
import fqf.qua_mario.oldmariostates.OldMarioState;
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
				OldMarioState landingResult = AirborneTransitions.LANDING.evaluate();
				if(landingResult != null) {
					MarioClient.doubleJumpLandingTime = 3;
					return landingResult;
				}
				return null;
			},
			AirborneTransitions.GROUND_POUND
		));
	}
}
