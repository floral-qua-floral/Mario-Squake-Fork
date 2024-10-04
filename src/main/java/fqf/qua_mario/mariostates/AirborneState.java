package fqf.qua_mario.mariostates;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.states.Grounded;
import fqf.qua_mario.mariostates.states.Jump;
import fqf.qua_mario.stomptypes.StompType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class AirborneState extends MarioState {
	protected boolean isJump;
	@Nullable protected CharaStat jumpCapStat;
	@Nullable protected StompType stompType;

	protected abstract void airTick();

	protected record AirborneTransitions() {
		public static final MarioStateTransition LANDING = () -> {
			if(MarioClient.player.isOnGround()) {
				if(MarioClient.getState() == Jump.INSTANCE) {
					MarioClient.jumpLandingTime = 4;
				}
				return Grounded.INSTANCE;
			}
			return null;
		};

		public static final MarioStateTransition DOUBLE_JUMPABLE_LANDING = () -> {
			MarioState landingResult = LANDING.evaluate();
			if(landingResult != null) {
				MarioClient.jumpLandingTime = 6;
				return landingResult;
			}
			return null;
		};

		public static final MarioStateTransition GROUND_POUND = () -> {
			if(Input.DUCK.isPressed()) {
				ModMarioQuaMario.LOGGER.info("Ground pound!");
			}
			return null;
		};
	}

	@Override
	public void tick() {
		airTick();

		if(jumpCapStat != null) {
			capJumpAndApplyGravity(jumpCapStat);
		}
		else {
			applyGravity(isJump ? CharaStat.JUMP_GRAVITY : CharaStat.GRAVITY);
		}

		if(stompType != null) {
			stompType.attemptStomp();
		}
	}
}
