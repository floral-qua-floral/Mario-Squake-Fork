package squeek.quakemovement;

import com.mojang.brigadier.Message;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

public class MarioClientTravel {
	private static final Logger LOGGER = ModQuakeMovement.LOGGER;

	public static class InvalidMarioStateException extends Exception {
		public InvalidMarioStateException(String errorMessage) {
			super(errorMessage);
		}
	}

	enum State {
		DEBUG
	}
	private static State marioState = State.DEBUG;

	public static boolean mario_travel(PlayerEntity player, Vec3d movementInput) throws InvalidMarioStateException {
		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double forwardX = -Math.sin(yawRad);
		double forwardZ = Math.cos(yawRad);
		double rightwardX = forwardZ;
		double rightwardZ = -forwardX;

		// Calculate current forwards and sideways velocity
		Vec3d currentVel = player.getVelocity();
		double forwardVel = currentVel.x * forwardX + currentVel.z * forwardZ;
		double rightwardVel = currentVel.x * rightwardX + currentVel.z * rightwardZ;

		// Evaluate the direction the player is inputting
		double intendedForwardVel = movementInput.z;
		double intendedRightwardVel = movementInput.x;
		double holdForward = (Math.abs(intendedForwardVel) < 0.1) ? 0.0 : Math.signum(intendedForwardVel);
		double holdSide = (Math.abs(intendedRightwardVel) < 0.1) ? 0.0 : Math.signum(intendedRightwardVel);

		// Behave differently depending on Mario's state
		switch(marioState) {
			case DEBUG:
//				accelPlayer(player, movementInput.z * 0.1, movementInput.x * 0.15, 0.5, -0.25, 0.35);
////				LOGGER.info(String.valueOf(player.getVelocity()));
//				player.setVelocity(movementInput.x, 0, movementInput.z);

				forwardVel = intendedForwardVel;
				rightwardVel = intendedRightwardVel;
			break;

			default:
				throw new InvalidMarioStateException("Mario state not handled in switch-case!!!");
		}

		// Calculate new speed
		double newXVel = forwardX * forwardVel + rightwardX * rightwardVel;
		double newZVel = forwardZ * forwardVel + rightwardZ * rightwardVel;
		player.setVelocity(newXVel, 0, newZVel);

		player.move(MovementType.SELF, player.getVelocity());
		player.updateLimbs(true);

		return true;
	}

	public static void accelPlayer(PlayerEntity player, double forward, double rightward, double forwardCap, double backwardCap, double sideCap) {
		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double forwardX = -Math.sin(yawRad);
		double forwardZ = Math.cos(yawRad);
		double rightwardX = forwardZ;
		double rightwardZ = -forwardX;

		// Calculate current forwards and sideways velocity
		Vec3d currentVel = player.getVelocity();
		double forwardVel = currentVel.x * forwardX + currentVel.z * forwardZ;
		double rightwardVel = currentVel.x * rightwardX + currentVel.z * rightwardZ;

		// Apply speed caps
		if (forward > 0) {
			if(forwardVel > forwardCap) forward = 0; // don't accelerate if we're already past the speed cap!
			else if(forwardVel + forward > forwardCap) {
				forward = forwardCap - forwardVel; // accelerate just enough to reach the cap
			}
		} else if (forward < 0) {
			if(forwardVel < backwardCap) forward = 0; // don't accelerate backwards if we're moving backwards too fast!
			else if(forwardVel + forward < backwardCap) {
				forward = backwardCap - forwardVel; // accelerate just enough to reach the cap
			}
		}

		// Sideways speed cap only applies if we're already moving in the direction we're trying to accelerate
		if(Math.signum(rightwardVel) == Math.signum(rightward)) {
			if (Math.abs(rightwardVel) > sideCap) {
				rightward = 0;
			} else if (Math.abs(rightwardVel + forward) > sideCap) {
				rightward = (sideCap * Math.signum(rightward)) - rightwardVel;
			}
		}

		// Calculate new speed
		double deltaXVel = forwardX * forward + rightwardX * rightward;
		double deltaZVel = forwardZ * forward + rightwardZ * rightward;
		Vec3d newVel = currentVel.add(deltaXVel, 0, deltaZVel);

		// If new speed is faster than current speed, cap it:
		if(newVel.horizontalLengthSquared() > currentVel.horizontalLengthSquared()) {
			// Calculate the overall speed cap
			double speedCap = Math.max(Math.max(forwardCap, Math.abs(backwardCap)), sideCap);
			double speedCapSquared = speedCap * speedCap;

			// Prevent the player from accelerating past it
			// This is a soft speed cap - if the player somehow gets past it, they can keep the speed
			if(currentVel.horizontalLengthSquared() > speedCapSquared) {
				// If old speed was already faster than the cap, set the new speed's magnitude to the old speed's magnitude
				// This allows the player to use their acceleration to change the angle of movement but not to go faster.
				newVel = newVel.normalize().multiply(currentVel.horizontalLength());
			} else if(newVel.horizontalLengthSquared() > speedCapSquared) {
				// If old speed was below the cap and new speed is above it, set new speed's magnitude to the cap.
				// This lets the player accelerate riiiiight up to the speed cap.
				newVel = newVel.normalize().multiply(speedCap);
			}
		}


		// Apply new speed
		player.setVelocity(newVel);
	}
}
