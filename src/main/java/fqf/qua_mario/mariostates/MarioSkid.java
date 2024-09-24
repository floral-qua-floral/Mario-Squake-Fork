package fqf.qua_mario.mariostates;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import fqf.qua_mario.cameraanims.CameraSideflip;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioSkid extends MarioState {
	public static final MarioSkid INSTANCE = new MarioSkid();

	private MarioSkid() {
		this.name = "Skid";

		preTickTransitions = new ArrayList<MarioStateTransition>(Arrays.asList(new MarioStateTransition[]{
				CommonTransitions.FALL,
				() -> {
					// Stop skidding
					if(MarioClient.stateTimer > 60 || MarioClient.forwardInput >= 0 || MarioClient.forwardVel < -0.05) {
						LOGGER.info("\n" + MarioClient.stateTimer + "\n" + MarioClient.forwardInput + "\n" + MarioClient.forwardVel);
						return MarioGrounded.INSTANCE;
					}
					return null;
				},
		}));

		postTickTransitions = new ArrayList<MarioStateTransition>(Arrays.asList(new MarioStateTransition[]{
				() -> {
					// Sideflip
					if(MarioInputs.isPressed(MarioInputs.Key.JUMP)) {
						MarioInputs.unbuffer(MarioInputs.Key.JUMP);
						MarioClient.yVel = 1.15;
						MarioClient.setMotion(-0.3, 0.0);
						MarioClient.player.setYaw(MarioClient.player.getYaw() + 180);
						LOGGER.info("\nPitch: " + MarioClient.player.getPitch() + "\nDelta: " + ((-180) - (2 * MarioClient.player.getPitch())));
//						CameraSideflip.deltaPitch = (-180) - (2 * MarioClient.player.getPitch());
						MarioClient.changeCameraAnim(CameraSideflip.INSTANCE);
						return MarioSideflip.INSTANCE;
					}
					return null;
				},
		}));


	}

	@Override
	public void tick() {
		MarioClient.yVel = -0.1;


		if(MarioClient.forwardInput < 0 && MarioClient.forwardVel > 0) {
			if(MarioClient.forwardVel <= 0.1) {
				LOGGER.info("\n" + MarioClient.stateTimer + "\n" + MarioClient.forwardVel + "\n<= 0.1: " + (MarioClient.forwardVel <= 0.1));
				MarioClient.stateTimer++;
			}
			MarioClient.setMotion(MarioClient.forwardVel * 0.9, 0);
		}
	}
}
