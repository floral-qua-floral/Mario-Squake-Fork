package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class Sideflip extends AirborneState {
	public static final Sideflip INSTANCE = new Sideflip();

	private Sideflip() {
		this.name = "Sideflip";
		this.isJump = true;
		this.jumpCapStat = CharaStat.SIDEFLIP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
				AirborneTransitions.DOUBLE_JUMPABLE_LANDING,
				AirborneTransitions.GROUND_POUND
		));
	}

	@Override
	public void airTick() {
		MarioClient.stateTimer++;
		if(MarioClient.stateTimer > 5)
			super.airTick();
	}
}
