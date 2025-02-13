package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;
import fqf.qua_mario.oldmariostates.OldMarioState;
import fqf.qua_mario.oldmariostates.states.groundbound.Grounded;
import fqf.qua_mario.oldmariostates.states.groundbound.Salute;
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
				OldMarioState landingResult = AirborneTransitions.LANDING.evaluate();
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
}
