package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class Stomp extends AirborneState {
	public static final Stomp INSTANCE = new Stomp();

	private Stomp() {
		this.name = "Stomp";
		this.isJump = true;
		this.jumpCapStat = CharaStat.STOMP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
			AirborneTransitions.DOUBLE_JUMPABLE_LANDING,
			AirborneTransitions.GROUND_POUND
		));
	}
}
