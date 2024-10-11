package fqf.qua_mario.oldmariostates;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.SoundFader;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.cameraanims.animations.CameraTripleJump;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.states.airborne.Aerial;
import fqf.qua_mario.oldmariostates.states.airborne.DoubleJump;
import fqf.qua_mario.oldmariostates.states.airborne.Jump;
import fqf.qua_mario.oldmariostates.states.airborne.TripleJump;
import fqf.qua_mario.stomptypes.StompType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GroundedState extends OldMarioState {
	@Nullable protected StompType stompType;

	protected abstract void groundedTick();

	@Override
	public void tick() {
		MarioClient.yVel -= 0.02;
		groundedTick();
	}

	public record GroundedTransitions() {
		public static final MarioStateTransition FALL = () -> {
			if(!MarioClient.player.isOnGround()) {
				MarioClient.yVel = Math.max(0.0, MarioClient.yVel);
				return Aerial.INSTANCE;
			}
			return null;
		};

		public static void performJump(@NotNull CharaStat velocityStat, @Nullable CharaStat addendStat) {
			performJump(velocityStat, addendStat, true);
		}

		public static void performJump(@NotNull CharaStat velocityStat, @Nullable CharaStat addendStat, boolean soundEffect) {
			MarioClient.jumpCapped = false;
			if(soundEffect) SoundFader.broadcastAndPlayJumpSound();

			MarioClient.yVel = velocityStat.getValue();
			if(addendStat != null) {
				double momentum = Math.max(0, MarioClient.forwardVel / CharaStat.P_SPEED.getValue());
				MarioClient.yVel += momentum * CharaStat.JUMP_VELOCITY_ADDEND.getValue();
			}
		}

		public static OldMarioState getJumpState(boolean executeNormalJump) {
			if(MarioClient.doubleJumpLandingTime > 0 && MarioClient.forwardVel > CharaStat.ADVANCED_JUMP_THRESHOLD.getValue()) { // Triple Jump
				performJump(CharaStat.TRIPLE_JUMP_VELOCITY, null);
				MarioClient.setCameraAnim(CameraTripleJump.INSTANCE);
				VoiceLine.TRIPLE_JUMP.broadcast();

				MarioClient.assignForwardStrafeVelocities(CharaStat.P_SPEED.getValue(), 0);

				return TripleJump.INSTANCE;
			}
			else if(MarioClient.jumpLandingTime > 0 && MarioClient.forwardVel > CharaStat.ADVANCED_JUMP_THRESHOLD.getValue()) { // Double Jump
				performJump(CharaStat.DOUBLE_JUMP_VELOCITY, CharaStat.DOUBLE_JUMP_VELOCITY_ADDEND);
				VoiceLine.DOUBLE_JUMP.broadcast();
				MarioClient.applyDrag(CharaStat.JUMP_SPEED_LOSS, CharaStat.ZERO);

				return DoubleJump.INSTANCE;
			}
			else { // Normal jump
				if(executeNormalJump) {
					performJump(CharaStat.JUMP_VELOCITY, CharaStat.JUMP_VELOCITY_ADDEND);
					MarioClient.applyDrag(CharaStat.JUMP_SPEED_LOSS, CharaStat.ZERO);
				}

				return Jump.INSTANCE;
			}
		}

		public static final MarioStateTransition JUMP = () -> {
			if(Input.JUMP.isPressed()) {
				return getJumpState(true);
			}
			return null;
		};
	}
}
