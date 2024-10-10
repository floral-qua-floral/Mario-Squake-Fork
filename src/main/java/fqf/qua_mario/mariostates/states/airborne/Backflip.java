package fqf.qua_mario.mariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class Backflip extends AirborneState {
	public static final Backflip INSTANCE = new Backflip();

	private Backflip() {
		this.name = "Backflip";
		this.isJump = true;
		this.jumpCapStat = CharaStat.BACKFLIP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
				AirborneTransitions.DOUBLE_JUMPABLE_LANDING,
				AirborneTransitions.GROUND_POUND
		));
	}

	@Override
	public void airTick() {
		MarioClient.stateTimer++;
		if(MarioClient.stateTimer > 8)
			super.airTick();
	}
}
