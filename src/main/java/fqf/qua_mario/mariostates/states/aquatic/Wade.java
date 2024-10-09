package fqf.qua_mario.mariostates.states.aquatic;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.mariostates.AquaticState;
import fqf.qua_mario.mariostates.states.airborne.Aerial;
import net.minecraft.registry.tag.FluidTags;

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