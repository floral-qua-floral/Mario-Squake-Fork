package fqf.qua_mario.mariostates;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import net.minecraft.registry.tag.FluidTags;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioUnderwater extends MarioState {
	public static final MarioUnderwater INSTANCE = new MarioUnderwater();

	private static int ticksSincePaddle = 1;

	private MarioUnderwater() {
		this.name = "Underwater";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					if(MarioClient.player.getFluidHeight(FluidTags.WATER) < 0.4) {
						MarioClient.yVel = Math.max(MarioClient.yVel, 0.65);
						return MarioAerial.INSTANCE;
					}
					return null;
				}
		));
	}

	@Override
	public void tick() {
//		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
//		applyGravity(-0.045, -0.4);
//		MarioClient.player.setVelocity(MarioClient.player.getVelocity().multiply(0.85));

		if(MarioInputs.isPressed(MarioInputs.Key.JUMP) && ticksSincePaddle > 2) {
//			MarioClient.yVel = Math.max(0.5, MarioClient.yVel + 0.4);
			ticksSincePaddle = 0;
		}
		else ticksSincePaddle++;
	}
}