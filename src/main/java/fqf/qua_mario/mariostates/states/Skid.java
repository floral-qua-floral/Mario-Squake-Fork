package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.cameraanims.animations.CameraSideflip;
import fqf.qua_mario.cameraanims.animations.CameraSideflipGentle;
import fqf.qua_mario.cameraanims.animations.CameraSideflipNoFunAllowed;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;

public class Skid extends MarioState {
	public static final Skid INSTANCE = new Skid();

	private Skid() {
		this.name = "Skid";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				CommonTransitions.FALL,
				() -> {
					// Stop skidding
					if (MarioClient.stateTimer > 60 || MarioClient.forwardInput >= 0 || MarioClient.forwardVel < -0.05) {
						LOGGER.info("\n" + MarioClient.stateTimer + "\n" + MarioClient.forwardInput + "\n" + MarioClient.forwardVel);
						return Grounded.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					return null;
				},
				() -> {
					// Sideflip
					if (MarioInputs.isPressed(MarioInputs.Key.JUMP)) {
						MarioInputs.unbuffer(MarioInputs.Key.JUMP);
						ModMarioQuaMario.LOGGER.info("yVel: " + MarioClient.getStat(CharaStat.JUMP_VELOCITY));
						MarioClient.yVel = 1.15;
						MarioClient.setMotion(-0.3, 0.0);
						MarioClient.player.setYaw(MarioClient.player.getYaw() + 180);

						switch(ModMarioQuaMario.CONFIG.getSideflipAnimType()) {
							case AUTHENTIC:
								MarioClient.setCameraAnim(CameraSideflip.INSTANCE);
								break;

							case GENTLE:
								CameraSideflipGentle.deltaPitch = (-180) - (2 * MarioClient.player.getPitch());
								MarioClient.setCameraAnim(CameraSideflipGentle.INSTANCE);
								break;

							case NO_FUN_ALLOWED:
								MarioClient.setCameraAnim(CameraSideflipNoFunAllowed.INSTANCE);
								break;
						}

						MarioClient.setCameraAnim(CameraSideflip.INSTANCE);
						return Sideflip.INSTANCE;
					}
					return null;
				}
		));


	}

	@Override
	public void tick() {
		MarioClient.yVel = -0.1;


		if(MarioClient.forwardInput < 0) {
			if(MarioClient.forwardVel <= 0.1) {
				MarioClient.stateTimer++;
			}
			MarioClient.setMotion(MarioClient.forwardVel * 0.9, 0);
		}
	}
}
