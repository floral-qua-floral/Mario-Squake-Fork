package fqf.qua_mario.mariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class PJump extends AirborneState {
	public static final PJump INSTANCE = new PJump();

	private PJump() {
		this.name = "P-Speed Jump";
		this.isJump = true;
		this.jumpCapStat = CharaStat.JUMP_CAP;
		this.stompType = StompBasic.INSTANCE;

		preTickTransitions = new ArrayList<>(Arrays.asList(
			AirborneTransitions.DOUBLE_JUMPABLE_LANDING,
			AirborneTransitions.GROUND_POUND
		));
	}

	@Override
	public void airTick() {
		aerialDrift(
				CharaStat.DRIFT_FORWARD_ACCEL, CharaStat.DRIFT_FORWARD_SPEED,
				CharaStat.DRIFT_BACKWARD_ACCEL, CharaStat.DRIFT_BACKWARD_SPEED,
				CharaStat.DRIFT_SIDE_ACCEL, CharaStat.DRIFT_SIDE_SPEED,
				CharaStat.P_SPEED_REDIRECTION
		);
	}
}
