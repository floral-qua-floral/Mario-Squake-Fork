package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.cameraanims.animations.CameraSideflip;
import fqf.qua_mario.cameraanims.animations.CameraSideflipGentle;
import fqf.qua_mario.cameraanims.animations.CameraSideflipNoFunAllowed;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;
import net.minecraft.util.math.MathHelper;

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
					if (MarioClient.stateTimer > 20 || MarioClient.forwardInput >= 0 || MarioClient.forwardVel < -0.05) {
						return Grounded.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					// Regular jump
					return(MarioClient.forwardVel > CharaStat.SIDEFLIP_THRESHOLD.getValue()
							? CommonTransitions.JUMP.evaluate() : null);
				},
				() -> {
					// Sideflip
					if (Input.JUMP.isPressed()) {
						ModMarioQuaMario.LOGGER.info("yVel: " + CharaStat.JUMP_VELOCITY.getValue());
						MarioClient.yVel = CharaStat.SIDEFLIP_VELOCITY.getValue();
						MarioClient.assignForwardStrafeVelocities(CharaStat.SIDEFLIP_BACKWARD_SPEED.getValue(), 0.0);
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
			if(MathHelper.approximatelyEquals(MarioClient.forwardVel, 0)) {
				MarioClient.stateTimer++;
			}
			MarioClient.applyDrag(CharaStat.SKID_DRAG);
//			MarioClient.setMotion(MarioClient.forwardVel * 0.9, 0);
//			MarioClient.assignForwardStrafeVelocities(MarioClient.forwardVel * CharaStat.SKID_DRAG.getValue(), MarioClient.rightwardVel * CharaStat.SKID_DRAG.getValue());
		}
	}
}
