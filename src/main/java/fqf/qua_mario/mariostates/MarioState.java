package fqf.qua_mario.mariostates;

import fqf.qua_mario.*;
import fqf.qua_mario.cameraanims.animations.CameraTripleJump;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.states.*;
import net.minecraft.entity.MovementType;
import net.minecraft.registry.tag.FluidTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class MarioState {
	@FunctionalInterface
	public interface MarioStateTransition {
		@Nullable MarioState evaluate();
	}

	protected String name;
	protected ArrayList<MarioStateTransition> preTickTransitions;
	protected ArrayList<MarioStateTransition> postTickTransitions;
	protected ArrayList<MarioStateTransition> postMoveTransitions = new ArrayList<>(Arrays.asList(
			CommonTransitions.LAVA_BOOST,
			CommonTransitions.ENTER_WATER
	));

	public enum TransitionPhases {
		PRE_TICK,
		POST_TICK,
		POST_MOVE,
		POST_STATE;

		private ArrayList<MarioStateTransition> getTransitionList(MarioState state) {
			return switch(this) {
				case PRE_TICK -> state.preTickTransitions;
				case POST_TICK -> state.postTickTransitions;
				case POST_MOVE -> state.postMoveTransitions;
				case POST_STATE -> null;
			};
		}
	}

	public String getName() {
		return name;
	}
	public boolean getSneakLegality() {
		return true;
	}

	public void evaluateTransitions(TransitionPhases phase) {
		MarioState powerTransitionResult = MarioClient.powerUp.customTransition(this, phase);
		if(MarioClient.changeState(powerTransitionResult)) return;

		ArrayList<MarioStateTransition> transitionList = phase.getTransitionList(this);
		if(transitionList != null) for (MarioStateTransition transition : transitionList) {
			MarioState transitionResult = transition.evaluate();
			if(transitionResult != null && MarioClient.changeState(transitionResult.getTransitionTarget(MarioClient.getState())))
				return;
		}
	}

	public MarioState getTransitionTarget(MarioState from) {
		MarioState stateFromPowerUp = MarioClient.powerUp.interceptTransition(from, this);
		return stateFromPowerUp != null ? stateFromPowerUp : this;
	}

	public abstract void tick();



	public record CommonTransitions() {
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

		public static MarioState getJumpState() {
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
				performJump(CharaStat.JUMP_VELOCITY, CharaStat.JUMP_VELOCITY_ADDEND);
				MarioClient.applyDrag(CharaStat.JUMP_SPEED_LOSS, CharaStat.ZERO);

				return Jump.INSTANCE;
			}
		}

		public static final MarioStateTransition JUMP = () -> {
			if(Input.JUMP.isPressed()) {
				return getJumpState();
			}
			return null;
		};

		public static final MarioStateTransition ENTER_WATER = () -> {
			if(MarioClient.player.getFluidHeight(FluidTags.WATER) > 0.5) {
				return Underwater.INSTANCE;
			}
			return null;
		};

		public static final MarioStateTransition LAVA_BOOST = () -> {
			if(MarioClient.player.isInLava()) {
				MarioClient.player.move(MovementType.SELF, MarioClient.player.getVelocity().multiply(-1));
				MarioClient.yVel = CharaStat.LAVA_BOOST_VEL.getValue();
				VoiceLine.BURNT.broadcast();
				return LavaBoost.INSTANCE;
			}
			return null;
		};
	}
}

