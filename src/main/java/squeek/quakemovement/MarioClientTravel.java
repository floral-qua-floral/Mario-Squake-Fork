package squeek.quakemovement;

import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

public class MarioClientTravel {
	private static final Logger LOGGER = ModQuakeMovement.LOGGER;

	public static class InvalidMarioStateException extends Exception {
		public InvalidMarioStateException(String errorMessage) {
			super(errorMessage);
		}
	}

	public static double forwardVel;
	public static double rightwardVel;

	enum State {
		DEBUG,
		GROUNDED
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
		forwardVel = currentVel.x * forwardX + currentVel.z * forwardZ;
		rightwardVel = currentVel.x * rightwardX + currentVel.z * rightwardZ;

		// Evaluate the direction the player is inputting
		Vec3d inputVector = movementInput.multiply(1);
		if(inputVector.horizontalLengthSquared() > 1) inputVector = inputVector.normalize();

		double forwardInput = (Math.abs(inputVector.z) < 0.1) ? 0.0 : inputVector.z;
		double rightwardInput = (Math.abs(inputVector.x) < 0.1) ? 0.0 : inputVector.x;

		double intendedForwardVel = 0;
		double intendedRightwardVel = 0;

		double yVel = currentVel.y;

		// Behave differently depending on Mario's state
		switch(marioState) {
			case DEBUG:
//				accelPlayer(player, movementInput.z * 0.35, movementInput.x * 0.45, 0.5, -0.25, 0.35);
////				LOGGER.info(String.valueOf(player.getVelocity()));
//				player.setVelocity(movementInput.x, 0, movementInput.z);

				intendedForwardVel = forwardInput * 0.5;
				intendedRightwardVel = rightwardInput * 0.5;
			break;

			case GROUNDED:
				double forwardAccel = 0;
				double rightwardAccel = 0;
				if(forwardInput > 0) {
//					forwardVel = approachNumber(forwardVel, forwardInput * 0.1, 1);
					if (forwardVel <= forwardInput) {
						// Walk Accel
						forwardAccel = forwardInput * 0.1;
					} else {
						// Overwalk
						forwardAccel = -0.05;
					}
				} else if(forwardInput < 0) {
					if(forwardVel > 1000) {
						// Transition to skid!
					} else if(forwardVel >= forwardInput) {
						// Backup Accel
					} else {
						// Overbackup
					}
				} else {
					// Not moving forward or backwards
					forwardAccel = 0;
				}

//				accelPlayer(player, forwardAccel, rightwardAccel, 1, 1, 1);
			break;

			default:
				throw new InvalidMarioStateException("Mario state not handled in switch-case!!!");
		}

		// Make relative velocities approach intended relative velocities
		double deltaForwardVel = 0.005 * Math.signum(intendedForwardVel - forwardVel);
		if(Math.abs(deltaForwardVel) >= Math.abs(forwardVel - intendedForwardVel))
			forwardVel = intendedForwardVel;
		else
			forwardVel += deltaForwardVel;

		double deltaRightwardVel = 0.1 * Math.signum(intendedRightwardVel - rightwardVel);
		if(Math.abs(deltaRightwardVel) >= Math.abs(rightwardVel - intendedRightwardVel))
			rightwardVel = intendedRightwardVel;
		else
			rightwardVel += deltaRightwardVel;

		// Calculate new cardinal velocities
		double newXVel = forwardX * forwardVel + rightwardX * rightwardVel;
		double newZVel = forwardZ * forwardVel + rightwardZ * rightwardVel;
		Vec3d newVel = new Vec3d(newXVel, 0, newZVel);

		// But you can't accelerate to any speed that's higher than your highest intended velocity!
		if(newVel.horizontalLengthSquared() > currentVel.horizontalLengthSquared()) {
			double speedCap = Math.max(Math.abs(intendedForwardVel), Math.abs(intendedRightwardVel));
			double speedCapSquared = speedCap * speedCap;

			if(currentVel.horizontalLengthSquared() > speedCapSquared) {
				// We're already moving faster than we want to be, so how are we accelerating?????
				newVel = newVel.normalize().multiply(currentVel.horizontalLength());
				LOGGER.info("Moving faster than we want to but also accelerating??");
			} else if(newVel.horizontalLengthSquared() > speedCapSquared) {
				// We're just about to pass our speed cap, so limit our speed to match it
				newVel = newVel.normalize().multiply(speedCap);
				LOGGER.info("Matching speed cap");
			}
		}

		// Assign and use the new velocity
		player.setVelocity(newXVel, yVel, newZVel);
		player.move(MovementType.SELF, player.getVelocity());
		player.updateLimbs(false);

		return true;
	}

	public static double approachNumber(double start, double delta, double target) {
		start += delta;
		if(delta > 0) {
			if(start > target) return target;
		} else if(delta < 0 && start < target) return target;

		return start;
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
//		double forwardVel = currentVel.x * forwardX + currentVel.z * forwardZ;
//		double rightwardVel = currentVel.x * rightwardX + currentVel.z * rightwardZ;

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
