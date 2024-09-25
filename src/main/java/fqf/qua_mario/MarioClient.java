package fqf.qua_mario;

import fqf.qua_mario.characters.CharaMario;
import fqf.qua_mario.characters.MarioCharacter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import fqf.qua_mario.cameraanims.CameraAnim;
import fqf.qua_mario.mariostates.MarioGrounded;
import fqf.qua_mario.mariostates.MarioState;

import java.util.List;

public class MarioClient {
	public static ClientPlayerEntity player;
	public static boolean isMario;

	public static double forwardVel;
	public static double rightwardVel;
	public static double yVel;
	public static double forwardInput;
	public static double rightwardInput;

	private static MarioState marioState = MarioGrounded.INSTANCE;
	public static int stateTimer = 0;
	public static void changeState(MarioState newState) {
		stateTimer = 0;
		marioState = newState;
	}

	public static CameraAnim marioCameraAnim = null;
	public static float cameraAnimTimer = 0;
	public static float animStartTime;
	public static float animEndTime;
	public static void changeCameraAnim(CameraAnim newAnim) {
		cameraAnimTimer = 0;
		marioCameraAnim = newAnim;
		animStartTime = player.getWorld().getTime() + MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
		animEndTime = (float) (animStartTime + newAnim.duration);
	}

	public static MarioCharacter character = CharaMario.INSTANCE;

	public static boolean attempt_travel(PlayerEntity player, Vec3d movementInput) {
		// don't do special movement if this is running server-side
		if (!player.getWorld().isClient) return false;

		// don't do special movement if the server hasn't told us whether we're Mario
		if(!isMario) return false;

		// don't do special movement if special movement is disabled
		if (!ModQuakeMovement.CONFIG.isEnabled()) return false;

		// don't do special movement if the player is flying, or gliding with an Elytra
		if (player.getAbilities().flying || player.isFallFlying()) return false;

		// don't do special movement if the player is in a vehicle
		if (player.hasVehicle()) return false;

		// don't do special movement if the player is climbing
		if (player.isClimbing()) return false;

//        return quake_travel(player, movementInput);
		if (player instanceof ClientPlayerEntity clientPlayer) {
			return mario_travel(clientPlayer, movementInput);
		}
		return false;
//		return false;
	}

	private static boolean mario_travel(ClientPlayerEntity player, Vec3d movementInput) {
		MarioClient.player = player;

		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double forwardX = -Math.sin(yawRad);
		double forwardZ = Math.cos(yawRad);
		double rightwardX = forwardZ;
		double rightwardZ = -forwardX;

		// Update Mario's custom inputs class
		MarioInputs.update(player);

		// Calculate current forwards and sideways velocity
		Vec3d currentVel = player.getVelocity();
		forwardVel = currentVel.x * forwardX + currentVel.z * forwardZ;
		rightwardVel = currentVel.x * rightwardX + currentVel.z * rightwardZ;

		// Evaluate the direction the player is inputting
		// Input normally goes up to about 0.98, so this multiplication brings it up close to a clean 1.0
		forwardInput = (Math.abs(movementInput.z) < 0.1) ? 0.0 : movementInput.z * 1.02040816327;
		rightwardInput = (Math.abs(movementInput.x) < 0.1) ? 0.0 : movementInput.x * 1.02040816327;

		// Store y velocity to modify it separately from X and Z velocity
		yVel = currentVel.y;

		// Execute Mario's state behavior
		marioState.evaluateTransitions(marioState.preTickTransitions);
		marioState.tick();
		marioState.evaluateTransitions(marioState.postTickTransitions);

		// Apply new y velocity
		Vec3d newVel = player.getVelocity();
		player.setVelocity(newVel.x, yVel, newVel.z);

		// Use velocities
		player.move(MovementType.SELF, player.getVelocity());
		marioState.evaluateTransitions(marioState.postMoveTransitions);

		// Finish
		player.updateLimbs(false);
		return true;
	}

	public static List<Entity> getStompTargets(boolean onlyFromAbove) {
		List<Entity> stompTargets = MarioClient.player.getWorld().getOtherEntities(MarioClient.player, MarioClient.player.getBoundingBox());

		if(onlyFromAbove)
			stompTargets.removeIf(stompTarget -> MarioClient.player.getY() - MarioClient.yVel < stompTarget.getY() + stompTarget.getHeight());

		return stompTargets;
	}

	public static void setMotion(double forward, double rightward) {
		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double forwardX = -Math.sin(yawRad);
		double forwardZ = Math.cos(yawRad);
		double rightwardX = forwardZ;
		double rightwardZ = -forwardX;

		forwardVel = forward;
		rightwardVel = rightward;

		double newXVel = forwardX * forwardVel + rightwardX * rightwardVel;
		double newZVel = forwardZ * forwardVel + rightwardZ * rightwardVel;

		player.setVelocity(newXVel, yVel, newZVel);
	}

	public static void groundedAccel(double intendedForward, double intendedStrafe, double accel, double strafeAccel) {
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

	public static void aerialAccel(double forward, double rightward, double forwardCap, double backwardCap, double sideCap) {
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
		double newVelHorizontalLenSquared = newVel.horizontalLengthSquared();
		double currentVelHorizontalLenSquared = currentVel.horizontalLengthSquared();

		double epsilon = 0.01;

		double deltaSpeed = newVelHorizontalLenSquared - currentVelHorizontalLenSquared;
		if(deltaSpeed > epsilon) {
			double capSquared = cap * cap;

			double speedUnderCapDifference = currentVelHorizontalLenSquared - capSquared;
			if(speedUnderCapDifference > epsilon) {
				// We're already moving too fast, so just use the new angle and don't increase magnitude
				return newVel.normalize().multiply(currentVel.horizontalLength());
			} else if((newVelHorizontalLenSquared - capSquared) > -epsilon) {
				// We're just about to pass our speed cap, so limit our speed to match it
				return newVel.normalize().multiply(cap);
			}
		}

		return newVel;
	}
}
