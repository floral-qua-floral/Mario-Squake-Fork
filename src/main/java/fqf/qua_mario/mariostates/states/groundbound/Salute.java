package fqf.qua_mario.mariostates.states.groundbound;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.mariostates.GroundedState;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Salute extends GroundedState {
	public static final Salute INSTANCE = new Salute();

	public static boolean shouldSalute() {
		return(MarioClient.forwardVel == 0 && MarioClient.rightwardVel == 0
				&& MarioClient.forwardInput == 0 && MarioClient.rightwardInput == 0);
	}

	private Salute() {
		this.name = "Salute";

		preTickTransitions = new ArrayList<>(List.of(
				GroundedTransitions.FALL,
				() -> {
					if(
							MarioClient.stateTimer > 18 ||
							MarioClient.forwardVel != 0 || MarioClient.rightwardVel != 0 ||
							MarioClient.forwardInput != 0 || MarioClient.rightwardInput != 0
					) return Grounded.INSTANCE;
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(List.of(
				GroundedTransitions.JUMP
		));
	}

	@Override
	public void groundedTick() {
		MarioClient.stateTimer++;
	}
}
