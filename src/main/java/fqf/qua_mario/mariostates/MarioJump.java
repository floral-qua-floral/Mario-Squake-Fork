package fqf.qua_mario.mariostates;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarioJump extends MarioState {
	public static final MarioJump INSTANCE = new MarioJump();

	private MarioJump() {
		this.name = "Jump";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				CommonTransitions.LANDING,
				() -> {
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
//							return MarioAerial.INSTANCE;
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
