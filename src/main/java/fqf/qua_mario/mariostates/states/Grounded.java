package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grounded extends MarioState {
	public static final Grounded INSTANCE = new Grounded();

	private Grounded() {
		this.name = "Grounded";

		preTickTransitions = new ArrayList<>(List.of(
				CommonTransitions.FALL,
				() -> {
					// Skid
					if(MarioInputs.isHeld(MarioInputs.Key.BACKWARD)
							&& MarioClient.forwardVel > MarioClient.getStat(CharaStat.SKID_THRESHOLD)) {
						return Skid.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(List.of(
				CommonTransitions.JUMP
		));
	}

	@Override
	public void tick() {
		double strafeSpeed = MarioClient.rightwardInput * 0.275;
		double strafeAccel = 0.065;

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
				else if(MarioClient.forwardVel <= MarioClient.getStat(CharaStat.WALK_STANDSTILL_THRESHOLD)) {
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
			if(MarioClient.forwardVel < MarioClient.getStatBuffer(CharaStat.BACKPEDAL_SPEED)) {
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

		MarioClient.yVel = -0.1;
	}
}
