package fqf.qua_mario.mariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class Aerial extends AirborneState {
	public static final Aerial INSTANCE = new Aerial();

	private Aerial() {
		this.name = "Aerial";
		this.isJump = false;
		this.jumpCapStat = null;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
				AirborneTransitions.LANDING,
				AirborneTransitions.GROUND_POUND
		));
	}

	@Override
	public void airTick() {

//		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
	}
}
