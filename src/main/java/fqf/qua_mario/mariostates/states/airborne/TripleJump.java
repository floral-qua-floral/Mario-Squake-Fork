package fqf.qua_mario.mariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.mariostates.states.Grounded;
import fqf.qua_mario.mariostates.states.Salute;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class TripleJump extends AirborneState {
	public static final TripleJump INSTANCE = new TripleJump();

	private TripleJump() {
		this.name = "Triple Jump";
		this.isJump = true;
		this.jumpCapStat = CharaStat.TRIPLE_JUMP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
			() -> {
				MarioState landingResult = AirborneTransitions.LANDING.evaluate();
				if(landingResult != null) {
					if(MarioClient.forwardInput == 0 && MarioClient.rightwardInput == 0) {
						VoiceLine.GYMNAST_SALUTE.broadcast();
						MarioClient.assignForwardStrafeVelocities(0, 0);
						MarioClient.forwardVel = 0; MarioClient.rightwardVel = 0;
						return Salute.INSTANCE;
					}
					else return Grounded.INSTANCE;
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
