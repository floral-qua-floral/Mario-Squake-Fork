package fqf.qua_mario.oldmariostates.states.groundbound;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.GroundedState;
import fqf.qua_mario.oldmariostates.OldMarioState;
import fqf.qua_mario.oldmariostates.states.airborne.Jump;
import fqf.qua_mario.oldmariostates.states.airborne.PJump;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class PSpeedRun extends GroundedState {
	public static final PSpeedRun INSTANCE = new PSpeedRun();

	private PSpeedRun() {
		this.name = "P-Speed Run";

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
					if(Input.BACKWARD.isHeld()) {
						return Skid.INSTANCE;
					}
					return null;
				},
				() -> {
					// Lose P-speed
					double speedThreshold = MarioClient.getStatThreshold(CharaStat.RUN_SPEED);
					if(!MarioClient.player.isSprinting() || MarioClient.forwardInput <= 0.3 || Vector2d.lengthSquared(MarioClient.forwardVel, MarioClient.rightwardVel) <= speedThreshold * speedThreshold) {
						return Grounded.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(List.of(
				() -> {
					// Jump with P-Jump instead of regular jump
					if(Input.JUMP.isPressed()) {
						OldMarioState postJumpState = GroundedTransitions.getJumpState(false);
						if(postJumpState == Jump.INSTANCE) {
							GroundedTransitions.performJump(CharaStat.JUMP_VELOCITY, CharaStat.JUMP_VELOCITY_ADDEND);
							return PJump.INSTANCE;
						}
						return(postJumpState);
					}
					return null;
				}
		));
	}

	@Override
	public void groundedTick() {
		// Run Accel
		MarioClient.groundAccel(
				CharaStat.P_SPEED_ACCEL, CharaStat.P_SPEED, 1.0,
				CharaStat.STRAFE_ACCEL, CharaStat.STRAFE_SPEED, 0.65,
				CharaStat.P_SPEED_REDIRECTION
		);
	}
}
