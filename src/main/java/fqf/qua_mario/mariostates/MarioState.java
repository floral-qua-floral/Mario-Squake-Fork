package fqf.qua_mario.mariostates;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioPackets;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.states.Aerial;
import fqf.qua_mario.mariostates.states.Grounded;
import fqf.qua_mario.mariostates.states.Jump;
import fqf.qua_mario.mariostates.states.Underwater;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.MovementType;
import net.minecraft.registry.tag.FluidTags;
import fqf.qua_mario.MarioClient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class MarioState {
	@FunctionalInterface
	protected interface MarioStateTransition {
		@Nullable MarioState evaluate();
	}

	public String name;
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

	protected void applyGravity(double accel, double terminalVelocity) {
		if(MarioClient.yVel > terminalVelocity) {
			MarioClient.yVel += accel;
			if(MarioClient.yVel < terminalVelocity) MarioClient.yVel = terminalVelocity;
		}
	}
	protected void applyGravity(CharaStat gravity) {
		applyGravity(gravity.getValue(), CharaStat.TERMINAL_VELOCITY.getValue());
	}
	protected void applyGravity() {
		applyGravity(CharaStat.GRAVITY);
	}

	protected void capJumpAndApplyGravity(CharaStat jumpCapStat) {
		final double CAP_SPEED = jumpCapStat.getValue();
		if(!MarioClient.jumpCapped && MarioClient.yVel > (Input.JUMP.isHeld() ? 0 : CAP_SPEED))
			applyGravity(CharaStat.JUMP_GRAVITY);
		else {
			if(!MarioClient.jumpCapped) {
				MarioClient.jumpCapped = true;
				MarioClient.yVel = Math.min(MarioClient.yVel, CAP_SPEED);
			}
			applyGravity(CharaStat.GRAVITY);
		}
	}

	protected static class CommonTransitions {
		public static final MarioStateTransition FALL = () -> {
			if(!MarioClient.player.isOnGround()) {
				MarioClient.yVel = Math.max(0.0, MarioClient.yVel);
				return Aerial.INSTANCE;
			}
			return null;
		};

		public static final MarioStateTransition JUMP = () -> {
			if(Input.JUMP.isPressed()) {
				MarioClient.jumpCapped = false;
				if(MarioClient.doubleJumpLandingTime > 0) { // Triple Jump
					ModMarioQuaMario.LOGGER.info("Triple jump! Wa-ha!");
				}
				else if(MarioClient.jumpLandingTime > 0) { // Double Jump
					ModMarioQuaMario.LOGGER.info("Double jump! Yippee!");
				}
				else { // Normal jump
					// Apply upward velocity
					double momentum = Math.max(0, MarioClient.forwardVel / CharaStat.RUN_SPEED.getValue());
					MarioClient.yVel = CharaStat.JUMP_VELOCITY.getValue()
							+ (momentum * CharaStat.JUMP_VELOCITY_ADDEND.getValue());

					// Reduce horizontal velocities
					MarioClient.assignForwardStrafeVelocities(MarioClient.forwardVel * 0.85, MarioClient.rightwardVel * 0.85);

					// Send packet to play the jump sound
					ClientPlayNetworking.send(new MarioPackets.PlayJumpSfxPayload(false));

					return Jump.INSTANCE;
				}
			}
			return null;
		};

		public static final MarioStateTransition LANDING = () -> {
			if(MarioClient.player.isOnGround()) {
				if(MarioClient.getState() == Jump.INSTANCE) {
					MarioClient.jumpLandingTime = 4;
				}
				return Grounded.INSTANCE;
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
			}
			return null;
		};

		public static final ArrayList<MarioStateTransition> PRE_TICK_JUMP_TRANSITIONS = new ArrayList<>(Arrays.asList(
				() -> {
					MarioState landingResult = CommonTransitions.LANDING.evaluate();
					if(landingResult != null) {
						MarioClient.jumpLandingTime = 6;
						return landingResult;
					}
					return null;
				},
				() -> {
					if(Input.DUCK.isPressed()) {
						ModMarioQuaMario.LOGGER.info("Ground pound!");
					}
					return null;
				}
		));
	}
}

