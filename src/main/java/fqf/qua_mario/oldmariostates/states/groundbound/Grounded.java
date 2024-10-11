package fqf.qua_mario.oldmariostates.states.groundbound;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.GroundedState;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grounded extends GroundedState {
	public static final Grounded INSTANCE = new Grounded();

	private Grounded() {
		this.name = "Grounded";

		preTickTransitions = new ArrayList<>(List.of(
				GroundedTransitions.FALL,
				() -> {
					// Duckslide
					if(Input.DUCK.isHeld() && DuckSlide.enterDuckSlide()) {
						VoiceLine.DUCK.broadcast();
						MarioClient.applyDrag(CharaStat.DUCK_SLIDE_BOOST, CharaStat.ZERO);
						return DuckSlide.INSTANCE;
					}
					return null;
				},
				() -> {
					// Duck Waddle
					if(Input.DUCK.isHeld()) {
						VoiceLine.DUCK.broadcast();
						return DuckWaddle.INSTANCE;
					}
					return null;
				},
				() -> {
					// Skid
					if(Input.BACKWARD.isHeld()
							&& MarioClient.forwardVel > CharaStat.SKID_THRESHOLD.getValue()) {
						return Skid.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(Arrays.asList(
				GroundedTransitions.JUMP,
				() -> {
					double speedThreshold = MarioClient.getStatThreshold(CharaStat.RUN_SPEED);
					if(MarioClient.player.isSprinting() && MarioClient.forwardInput >= 0.7 && Vector2d.lengthSquared(MarioClient.forwardVel, MarioClient.rightwardVel) >= speedThreshold * speedThreshold) {
						Vector2d currentMotion = new Vector2d(MarioClient.forwardVel, MarioClient.rightwardVel);
						currentMotion.normalize(CharaStat.P_SPEED.getValue());
						MarioClient.assignForwardStrafeVelocities(currentMotion.x, currentMotion.y);
						return PSpeedRun.INSTANCE;
					}
					return null;
				}
		));
	}

	@Override
	public void groundedTick() {
		if(MarioClient.forwardInput > 0) {
			boolean isRunning = MarioClient.player.isSprinting()
					&& MarioClient.forwardVel > MarioClient.getStatThreshold(CharaStat.WALK_SPEED);

			if(isRunning) {
				if(MarioClient.forwardVel > MarioClient.getStatBuffer(CharaStat.RUN_SPEED)) {
					// Overrun
					MarioClient.groundAccel(
							CharaStat.OVERRUN_ACCEL, CharaStat.RUN_SPEED, 1.0,
							CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 0.8,
							CharaStat.RUN_REDIRECTION
					);
				}
				else {
					// Run Accel
					MarioClient.groundAccel(
							CharaStat.RUN_ACCEL, CharaStat.RUN_SPEED, 1.0,
							CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
							CharaStat.RUN_REDIRECTION
					);
				}
			}
			else {
				if(MarioClient.forwardVel > MarioClient.getStatBuffer(CharaStat.WALK_SPEED)) {
					// Overwalk
					MarioClient.groundAccel(
							CharaStat.OVERWALK_ACCEL, CharaStat.WALK_SPEED, 1.0,
							CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
							CharaStat.WALK_REDIRECTION
					);
				}
				else if(MarioClient.forwardVel <= CharaStat.WALK_STANDSTILL_THRESHOLD.getValue()) {
					// Walk accel from low velocity
					MarioClient.groundAccel(
							CharaStat.WALK_STANDSTILL_ACCEL, CharaStat.WALK_SPEED, 1.0,
							CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
							CharaStat.WALK_REDIRECTION
					);
				}
				else {
					// Walk accel
					MarioClient.groundAccel(
							CharaStat.WALK_ACCEL, CharaStat.WALK_SPEED, 1.0,
							CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
							CharaStat.WALK_REDIRECTION
					);
				}
			}
		}
		else if(MarioClient.forwardInput < 0) {
			if(MarioClient.forwardVel > 0) {
				// Under-backpedal
				MarioClient.groundAccel(
						CharaStat.UNDERBACKPEDAL_ACCEL, CharaStat.BACKPEDAL_SPEED, 1.0,
						CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
						CharaStat.BACKPEDAL_REDIRECTION
				);
			}
			else if(MarioClient.forwardVel < MarioClient.getStatBuffer(CharaStat.BACKPEDAL_SPEED)) {
				// Over-backpedal
				MarioClient.groundAccel(
						CharaStat.OVERBACKPEDAL_ACCEL, CharaStat.BACKPEDAL_SPEED, 1.0,
						CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
						CharaStat.BACKPEDAL_REDIRECTION
				);
			}
			else {
				// Backpedal Accel
				MarioClient.groundAccel(
						CharaStat.BACKPEDAL_ACCEL, CharaStat.BACKPEDAL_SPEED, 1.0,
						CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
						CharaStat.BACKPEDAL_REDIRECTION
				);
			}
		}
		else {
			// Idle deaccel
			MarioClient.groundAccel(
					CharaStat.IDLE_DEACCEL, CharaStat.ZERO, 1.0,
					CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 1.0,
					CharaStat.ZERO
			);
		}
	}
}
