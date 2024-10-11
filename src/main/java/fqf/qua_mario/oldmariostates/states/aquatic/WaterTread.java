package fqf.qua_mario.oldmariostates.states.aquatic;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.oldmariostates.AquaticState;
import fqf.qua_mario.oldmariostates.states.airborne.Aerial;
import net.minecraft.registry.tag.FluidTags;

import java.util.ArrayList;
import java.util.Arrays;

public class WaterTread extends AquaticState {
	public static final WaterTread INSTANCE = new WaterTread();

	private WaterTread() {
		this.name = "Treading Water";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					if(MarioClient.player.getFluidHeight(FluidTags.WATER) < 0.4) {
						MarioClient.yVel = Math.max(MarioClient.yVel, 0.65);
						return Aerial.INSTANCE;
					}
					return null;
				}
		));
	}

	@Override
	protected void waterTick() {

	}
}