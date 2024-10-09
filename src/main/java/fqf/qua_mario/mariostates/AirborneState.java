package fqf.qua_mario.mariostates;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.SoundFader;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.states.Grounded;
import fqf.qua_mario.stomptypes.StompHandler;
import fqf.qua_mario.stomptypes.StompType;
import org.jetbrains.annotations.Nullable;

public abstract class AirborneState extends MarioState {
	protected boolean isJump;
	@Nullable protected CharaStat jumpCapStat;
	@Nullable protected StompType stompType;

	protected abstract void airTick();

	@Override
	public boolean getSneakLegality() {
		return false;
	}

	@Override
	public void tick() {
		airTick();
		applyGravity();

		if(stompType != null && MarioClient.yVel < 0) {
			stompType.attemptStomp();
		}
	}

	protected void applyGravity() {
		if(jumpCapStat != null) {
			capJumpAndApplyGravity(jumpCapStat);
		}
		else {
			applyGravity(isJump ? CharaStat.JUMP_GRAVITY : CharaStat.GRAVITY);
		}
	}

	protected void capJumpAndApplyGravity(CharaStat jumpCapStat) {
		if(!MarioClient.jumpCapped && Input.JUMP.isHeld() && MarioClient.yVel > 0)
			applyGravity(CharaStat.JUMP_GRAVITY);
		else {
			if(!MarioClient.jumpCapped) {
				MarioClient.jumpCapped = true;
				MarioClient.yVel = Math.min(MarioClient.yVel, jumpCapStat.getValue());
				SoundFader.broadcastAndFadeJumpSound();
			}
			applyGravity(CharaStat.GRAVITY);
		}
	}

	protected void applyGravity(CharaStat gravity) {
		applyGravity(gravity.getValue(), CharaStat.TERMINAL_VELOCITY.getValue());
	}
	protected void applyGravity(double accel, double terminalVelocity) {
		if(MarioClient.yVel > terminalVelocity)
			MarioClient.yVel = Math.max(terminalVelocity, MarioClient.yVel + accel);
	}

	protected record AirborneTransitions() {
		public static final MarioStateTransition LANDING = () -> {
			if(MarioClient.player.isOnGround()) {
				StompHandler.forbiddenStompTargets.clear();
				return Grounded.INSTANCE;
			}
			return null;
		};

		public static final MarioStateTransition DOUBLE_JUMPABLE_LANDING = () -> {
			MarioState landingResult = LANDING.evaluate();
			if(landingResult != null) {
				MarioClient.jumpLandingTime = 3;
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
}
