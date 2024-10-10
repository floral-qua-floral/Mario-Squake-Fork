package fqf.qua_mario.mariostates;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.SoundFader;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.states.groundbound.Grounded;
import fqf.qua_mario.stomptypes.StompHandler;
import fqf.qua_mario.stomptypes.StompType;
import org.jetbrains.annotations.Nullable;

public abstract class AirborneState extends MarioState {
	protected boolean isJump;
	@Nullable protected CharaStat jumpCapStat;
	@Nullable protected StompType stompType;

	protected void airTick() {
		aerialDrift();
	}

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

	protected void aerialDrift() {
		aerialDrift(
				CharaStat.DRIFT_FORWARD_ACCEL, CharaStat.DRIFT_FORWARD_SPEED,
				CharaStat.DRIFT_BACKWARD_ACCEL, CharaStat.DRIFT_BACKWARD_SPEED,
				CharaStat.DRIFT_SIDE_ACCEL, CharaStat.DRIFT_SIDE_SPEED,
				CharaStat.DRIFT_REDIRECTION
		);
	}

	protected void aerialDrift(
			CharaStat forwardAccelStat, CharaStat forwardSpeedStat,
			CharaStat backwardAccelStat, CharaStat backwardSpeedStat,
			CharaStat sideAccelStat, CharaStat sideSpeedStat,
			CharaStat redirectionStat
	) {
		MarioClient.airborneAccel(
				MarioClient.forwardInput >= 0 ? forwardAccelStat : backwardAccelStat,
				MarioClient.forwardInput >= 0 ? forwardSpeedStat : backwardSpeedStat,
				1.0,
				sideAccelStat,
				sideSpeedStat,
				1.0,
				redirectionStat
		);
	}

	public record AirborneTransitions() {
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
