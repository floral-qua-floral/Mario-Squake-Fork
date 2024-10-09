package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class LavaBoost extends AirborneState {
	public static final LavaBoost INSTANCE = new LavaBoost();

	private LavaBoost() {
		this.name = "Lava Boost";
		this.isJump = false;
		this.jumpCapStat = null;
		this.stompType = null;

		preTickTransitions = new ArrayList<>(Arrays.asList(
				AirborneTransitions.LANDING
		));
	}

	@Override
	public void airTick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
	}
}
