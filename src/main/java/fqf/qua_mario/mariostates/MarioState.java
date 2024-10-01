package fqf.qua_mario.mariostates;

import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.states.Aerial;
import fqf.qua_mario.mariostates.states.Grounded;
import fqf.qua_mario.mariostates.states.Jump;
import fqf.qua_mario.mariostates.states.Underwater;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.MovementType;
import net.minecraft.registry.tag.FluidTags;
import org.slf4j.Logger;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class MarioState {
	@FunctionalInterface
	protected interface MarioStateTransition {
		MarioState evaluate();
	}

	protected static final Logger LOGGER = ModMarioQuaMario.LOGGER;

	public String name;
	public ArrayList<MarioStateTransition> preTickTransitions;
	public ArrayList<MarioStateTransition> postTickTransitions;
	public ArrayList<MarioStateTransition> postMoveTransitions = new ArrayList<>(Arrays.asList(
			CommonTransitions.LAVA_BOOST,
			CommonTransitions.ENTER_WATER
	));

	public void evaluateTransitions(ArrayList<MarioStateTransition> transitions) {
		if(transitions != null) for (MarioStateTransition transition : transitions) {
			MarioState nextState = transition.evaluate();
			if (nextState != null) {
				MarioClient.stateTimer = 0;
				MarioClient.changeState(nextState);
				return;
			}
		}
	}

	public abstract void tick();

	protected void applyGravity(double accel, double terminalVelocity) {
		if(MarioClient.yVel > terminalVelocity) {
			MarioClient.yVel += accel;
			if(MarioClient.yVel < terminalVelocity) MarioClient.yVel = terminalVelocity;
		}
	}
	protected void applyGravity(CharaStat gravity, CharaStat terminalVelocity) {
		applyGravity(MarioClient.getStat(gravity), MarioClient.getStat(terminalVelocity));
	}
	protected void applyGravity() {
		applyGravity(CharaStat.GRAVITY, CharaStat.TERMINAL_VELOCITY);
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
			if(MarioInputs.isPressed(MarioInputs.Key.JUMP)) { // Normal jump
				// Apply upward velocity
				double momentum = Math.max(0, MarioClient.forwardVel / MarioClient.getStat(CharaStat.RUN_SPEED));
				MarioClient.yVel = MarioClient.getStat(CharaStat.JUMP_VELOCITY)
						+ (momentum * MarioClient.getStat(CharaStat.JUMP_VELOCITY_ADDEND));

				// Reduce horizontal velocities
				MarioClient.forwardVel *= 0.85;
				MarioClient.rightwardVel *= 0.85;

				// Send packet to play the jump sound
				ClientPlayNetworking.send(new ModMarioQuaMario.PlayJumpSfxPayload(false));

				return Jump.INSTANCE;
			}
			return null;
		};

		public static final MarioStateTransition LANDING = () -> {
			if(MarioClient.player.isOnGround()) {
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
	}
}

