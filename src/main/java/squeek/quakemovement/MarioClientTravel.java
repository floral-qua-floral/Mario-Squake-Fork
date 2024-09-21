package squeek.quakemovement;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import squeek.quakemovement.mariostates.MarioGrounded;
import squeek.quakemovement.mariostates.MarioState;

import java.util.Objects;

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
		GROUNDED,
		AERIAL,
		JUMP
	}
	private static State marioState = State.GROUNDED;
	private static MarioState mariosState = MarioGrounded.INSTANCE;

	public static boolean mario_travel(ClientPlayerEntity player, Vec3d movementInput) throws InvalidMarioStateException {
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
		// Input normally goes up to about 0.98, so this multiplication brings it up close to a clean 1.0
		double forwardInput = (Math.abs(movementInput.z) < 0.1) ? 0.0 : movementInput.z * 1.02040816327;
		double rightwardInput = (Math.abs(movementInput.x) < 0.1) ? 0.0 : movementInput.x * 1.02040816327;

		// Store y velocity to modify it separately from X and Z velocity
		double yVel = currentVel.y;

		// Behave differently depending on Mario's state
		LOGGER.info(String.valueOf(mariosState.name));
		switch(marioState) {
			case DEBUG:
////				LOGGER.info(String.valueOf(player.getVelocity()));
//				player.setVelocity(movementInput.x, 0, movementInput.z);

//				intendedForwardVel = forwardInput * 0.5;
//				intendedRightwardVel = rightwardInput * 0.5;

				aerialAccel(player, movementInput.z * 0.35, movementInput.x * 0.45, 0.5, -0.25, 0.35);
			break;

			case GROUNDED:
				double strafeSpeed = rightwardInput * 0.3;
				double strafeAccel = 0.065;
				if(forwardInput > 0) {
					double forwardMoveSpeed = forwardInput * 0.4;
					if (forwardVel <= 0.23) {
						// Walk Accel From Standstill or Backpedal
						groundedAccel(player, forwardMoveSpeed, strafeSpeed, 0.125, strafeAccel);
					}
					else if (forwardVel <= forwardInput * 0.6) {
						// Walk Accel
						groundedAccel(player, forwardMoveSpeed, strafeSpeed, 0.045, strafeAccel);
					} else {
						// Overwalk
						groundedAccel(player, forwardMoveSpeed, strafeSpeed, 0.01, strafeAccel);
					}
				} else if(forwardInput < 0) {
					double forwardMoveSpeed = forwardInput * 0.3;
					if(forwardVel > 0.5) {
						// Transition to skid!
						LOGGER.info("TRANSITION TO SKID!!!!");
						LOGGER.info(String.valueOf(forwardInput));
						groundedAccel(player, 0, strafeSpeed, 0.1, strafeAccel);
					} else if(forwardVel >= forwardInput) {
						// Backpedal Accel
						groundedAccel(player, forwardMoveSpeed, strafeSpeed, 0.065, strafeAccel);
					} else {
						// Overbackup
						groundedAccel(player, forwardMoveSpeed, strafeSpeed, 0.045, strafeAccel);
					}
				} else {
					// Not moving forward or backwards
					groundedAccel(player, 0, strafeSpeed, 0.075, strafeAccel);
				}

				yVel = -0.1;

				if(player.input.jumping) {
					yVel = 0.875;
					marioState = State.JUMP;
				}
				else if(!player.isOnGround()) {
					marioState = State.AERIAL;
				}
			break;

			case JUMP:
				if(!player.input.jumping && yVel > 0.4) {
					yVel = 0.4;
				}
			case AERIAL:
				aerialAccel(player, forwardInput * 0.04, rightwardInput * 0.04, 0.25, -0.25, 0.195);
				if(!player.isSneaking()) yVel -= 0.1;

				if(player.isOnGround()) {
					marioState = State.GROUNDED;
				}
			break;

			default:
				throw new InvalidMarioStateException("Mario state not handled in switch-case!!!");
		}

		// Apply new y velocity
		Vec3d newVel = player.getVelocity();
		player.setVelocity(newVel.x, yVel, newVel.z);

		// Use velocities
		player.move(MovementType.SELF, player.getVelocity());
		player.updateLimbs(false);

		return true;
	}

	public static void groundedAccel(PlayerEntity player, double intendedForward, double intendedStrafe, double accel, double strafeAccel) {
		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double forwardX = -Math.sin(yawRad);
		double forwardZ = Math.cos(yawRad);
		double rightwardX = forwardZ;
		double rightwardZ = -forwardX;

		// Make relative velocities approach intended relative velocities
		double deltaForwardVel = accel * Math.signum(intendedForward - forwardVel);
		if(Math.abs(deltaForwardVel) >= Math.abs(forwardVel - intendedForward))
			forwardVel = intendedForward;
		else
			forwardVel += deltaForwardVel;

		double deltaRightwardVel = strafeAccel * Math.signum(intendedStrafe - rightwardVel);
		if(Math.abs(deltaRightwardVel) >= Math.abs(rightwardVel - intendedStrafe))
			rightwardVel = intendedStrafe;
		else
			rightwardVel += deltaRightwardVel;

		// Calculate new cardinal velocities and apply speed cap
		double newXVel = forwardX * forwardVel + rightwardX * rightwardVel;
		double newZVel = forwardZ * forwardVel + rightwardZ * rightwardVel;
		Vec3d currentVel = player.getVelocity();
		Vec3d newVel = capAcceleration(currentVel, new Vec3d(newXVel, 0, newZVel), Math.max(Math.abs(intendedForward), Math.abs(intendedStrafe)));

		// Assign and use the new velocity
		player.setVelocity(newVel.x, currentVel.y, newVel.z);
//		player.setVelocity(intendedForward, currentVel.y, intendedStrafe);
	}

	public static void aerialAccel(PlayerEntity player, double forward, double rightward, double forwardCap, double backwardCap, double sideCap) {
		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double forwardX = -Math.sin(yawRad);
		double forwardZ = Math.cos(yawRad);
		double rightwardX = forwardZ;
		double rightwardZ = -forwardX;

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
		Vec3d currentVel = player.getVelocity();
		double deltaXVel = forwardX * forward + rightwardX * rightward;
		double deltaZVel = forwardZ * forward + rightwardZ * rightward;
		Vec3d newVel = capAcceleration(currentVel, currentVel.add(deltaXVel, 0, deltaZVel),
				Math.max(Math.max(forwardCap, Math.abs(backwardCap)), sideCap));

		// Apply new speed
		player.setVelocity(newVel.x, currentVel.y, newVel.z);
	}

	public static Vec3d capAcceleration(Vec3d currentVel, Vec3d newVel, double cap) {
		// I have to do it like this with a hundred thousand variables or the math gets wonky!
		double newVelHorizLenSquared = newVel.horizontalLengthSquared();
		double currentVelHorizLenSquared = currentVel.horizontalLengthSquared();

		double epsilon = 0.01;

		double deltaSpeed = newVelHorizLenSquared - currentVelHorizLenSquared;
		if(deltaSpeed > epsilon) {
			double capSquared = cap * cap;

			double speedUnderCapDifference = currentVelHorizLenSquared - capSquared;
			if(speedUnderCapDifference > epsilon) {
				// We're already moving too fast, so just use the new angle and don't increase magnitude
				LOGGER.info("Accelerating but already moving faster than the speed cap");
				return newVel.normalize().multiply(currentVel.horizontalLength());
			} else if((newVelHorizLenSquared - capSquared) > -epsilon) {
				// We're just about to pass our speed cap, so limit our speed to match it
				LOGGER.info("Matching speed cap");
				return newVel.normalize().multiply(cap);
			}
		}

		return newVel;
	}

	public static boolean isJumping(PlayerEntity player) {
		return ((QuakeClientPlayer.IsJumpingGetter) player).isJumping();
	}
}
