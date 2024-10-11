package fqf.qua_mario.oldmariostates.states.groundbound;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.cameraanims.animations.CameraSideflip;
import fqf.qua_mario.cameraanims.animations.CameraSideflipGentle;
import fqf.qua_mario.cameraanims.animations.CameraSideflipNoFunAllowed;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.GroundedState;
import fqf.qua_mario.oldmariostates.states.airborne.Sideflip;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class Skid extends GroundedState {
	public static final Skid INSTANCE = new Skid();

	private Skid() {
		this.name = "Skid";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				GroundedTransitions.FALL,
				() -> {
					// Stop skidding
					if (MarioClient.stateTimer > 0 || MarioClient.forwardInput >= 0 || MarioClient.forwardVel < -0.05) {
						return Grounded.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					// Regular jump
					return(MarioClient.forwardVel > CharaStat.SIDEFLIP_THRESHOLD.getValue()
							? GroundedTransitions.JUMP.evaluate() : null);
				},
				() -> {
					// Sideflip
					if (Input.JUMP.isPressed()) {
						ModMarioQuaMario.LOGGER.info("yVel: " + CharaStat.JUMP_VELOCITY.getValue());
						GroundedTransitions.performJump(CharaStat.SIDEFLIP_VELOCITY, CharaStat.ZERO);
						VoiceLine.SIDEFLIP.broadcast();
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
	public void groundedTick() {
		if(MarioClient.forwardInput < 0) {
			if(MathHelper.approximatelyEquals(MarioClient.forwardVel, 0)) {
				MarioClient.stateTimer++;
			}
			MarioClient.applyDrag(CharaStat.SKID_DRAG, CharaStat.SKID_DRAG_MIN,
					CharaStat.SKID_REDIRECTION, -1, 0.5);
		}
	}
}
