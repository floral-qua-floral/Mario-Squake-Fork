package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class Jump extends AirborneState {
	public static final Jump INSTANCE = new Jump();

	private Jump() {
		this.name = "Jump";
		this.isJump = true;
		this.jumpCapStat = CharaStat.JUMP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
			AirborneTransitions.DOUBLE_JUMPABLE_LANDING,
			AirborneTransitions.GROUND_POUND
		));
	}
}
