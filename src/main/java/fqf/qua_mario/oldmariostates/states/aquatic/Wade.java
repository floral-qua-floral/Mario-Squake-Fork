package fqf.qua_mario.oldmariostates.states.aquatic;

import fqf.qua_mario.oldmariostates.AquaticState;

import java.util.ArrayList;
import java.util.Arrays;

public class Wade extends AquaticState {
	public static final Wade INSTANCE = new Wade();

	private Wade() {
		this.name = "Wading";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				AquaticTransitions.EXIT_WATER
		));
	}

	@Override
	protected void waterTick() {

	}
}