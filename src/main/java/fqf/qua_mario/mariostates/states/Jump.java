package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import fqf.qua_mario.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;

public class Jump extends MarioState {
	public static final Jump INSTANCE = new Jump();

	private Jump() {
		this.name = "Jump";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					MarioState landingResult = CommonTransitions.LANDING.evaluate();
					if(landingResult != null) {
						MarioClient.jumpLandingTime = 6;
						return landingResult;
					}
					return null;
				},
				() -> {
					if(MarioInputs.isPressed(MarioInputs.Key.SNEAK)) {
						// Initiate ground pound
					}
					return null;
				}
		));

//		postMoveTransitions = new ArrayList<>(Arrays.asList(
//				() -> {
//					if (MarioClient.yVel > 0 || MarioClient.yVel <= 0) return null;
//
//					List<Entity> stompTargets = MarioClient.getStompTargets(true);
//					for (Entity stompTarget : stompTargets) {
//						LOGGER.info("Attempt stomp against " + stompTarget);
//						if (false) {
//							double desiredY = stompTarget.getY() + stompTarget.getHeight();
//							double deltaY = MarioClient.player.getY() - desiredY;
//							MarioClient.player.move(MovementType.SELF, new Vec3d(0, deltaY, 0));
//							return Aerial.INSTANCE;
//						}
//					}
//
//					return null;
//				}
//		));
	}

	@Override
	public void tick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
		applyGravity();

		final double CAP_SPEED = 0.275;
		if((MarioInputs.isHeld(MarioInputs.Key.SNEAK) || !MarioInputs.isHeld(MarioInputs.Key.JUMP)) && MarioClient.yVel > CAP_SPEED) {
			MarioClient.yVel = CAP_SPEED;
		}
	}
}
