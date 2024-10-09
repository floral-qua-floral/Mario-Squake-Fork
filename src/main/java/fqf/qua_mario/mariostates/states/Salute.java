package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.List;

public class Salute extends MarioState {
	public static final Salute INSTANCE = new Salute();

	public static boolean shouldSalute() {
		return(MarioClient.forwardVel == 0 && MarioClient.rightwardVel == 0
				&& MarioClient.forwardInput == 0 && MarioClient.rightwardInput == 0);
	}

	private Salute() {
		this.name = "Salute";

		preTickTransitions = new ArrayList<>(List.of(
				CommonTransitions.FALL,
				() -> {
					if(
							MarioClient.stateTimer > 18 ||
							MarioClient.forwardVel != 0 || MarioClient.rightwardVel != 0 ||
							MarioClient.forwardInput != 0 || MarioClient.rightwardInput != 0 ||
							Input.JUMP.isPressed() || Input.SPIN.isPressed() || Input.DUCK.isPressed()
					) return Grounded.INSTANCE;
					return null;
				}
		));
	}

	@Override
	public void tick() {
		MarioClient.stateTimer++;
		MarioClient.yVel -= 0.1;
	}
}
