package fqf.qua_mario;

import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.mariostates.states.groundbound.Grounded;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.SuperForm;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import fqf.qua_mario.cameraanims.CameraAnim;
import fqf.qua_mario.mariostates.MarioState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class MarioClient {
	public static ClientPlayerEntity player;
	public static boolean isMario;

	public static double forwardVel;
	public static double rightwardVel;
	public static double yVel;
	public static double forwardInput;
	public static double rightwardInput;
	private static final double INPUT_FACTOR = 1.02040816327;

	private static MarioState marioState = Grounded.INSTANCE;
	public static int stateTimer = 0;
	public static boolean jumpCapped = false;
	public static boolean changeState(MarioState newState) {
		if(newState == null || marioState == newState) return false;

		if(marioState.getSneakLegality() != newState.getSneakLegality())
			ClientPlayNetworking.send(new MarioPackets.SetSneakingLegality(newState.getSneakLegality()));

		stateTimer = 0;
		marioState = newState;
		return true;
	}
	public static MarioState getState() { return(marioState); }

	public static int jumpLandingTime = 0;
	public static int doubleJumpLandingTime = 0;

	private static CameraAnim cameraAnim = null;
	public static float cameraAnimTimer = 0;
	public static float animStartTime;
	public static float animEndTime;
	public static void setCameraAnim(@Nullable CameraAnim newAnim) {
		cameraAnimTimer = 0;
		cameraAnim = newAnim;
//		animStartTime = player.getWorld().getTime() + MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
		animStartTime = player.getWorld().getTime() + 1;
		animEndTime = (float) (animStartTime + (newAnim == null ? 0 : newAnim.duration));
	}
	public static CameraAnim getCameraAnim() { return cameraAnim; }

	public static MarioCharacter character = CharaMario.INSTANCE;

	public static PowerUp powerUp = SuperForm.INSTANCE;

	public static boolean useCharacterStats = true;
	public static CharaStat lastUsedAccelStat;

	public static boolean attemptMarioTravel(PlayerEntity player, Vec3d movementInput) {
		if(ModMarioQuaMario.useMarioPhysics(player, true) && player instanceof ClientPlayerEntity clientPlayer) {
			clientPlayer.getWorld().getProfiler().push("marioTravel");
			boolean travelResult = marioTravel(clientPlayer, movementInput);
			clientPlayer.getWorld().getProfiler().pop();
			return travelResult;
		}
		return false;
	}

	private static boolean marioTravel(ClientPlayerEntity player, Vec3d movementInput) {
		MarioClient.player = player;

		// Update Mario's custom inputs class
		Input.update(player.input);

		// Undo vanilla vertical swimming
		if(player.input.pressingLeft)
			MarioClient.yVel += (player.input.sneaking ? 0.04 : 0.0) - (player.input.jumping ? 0.04 : 0.0);

		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double negativeSineYaw = -Math.sin(yawRad);
		double cosineYaw = Math.cos(yawRad);

		// Calculate current forwards and sideways velocity
		Vec3d currentVel = player.getVelocity();
		forwardVel = currentVel.x * negativeSineYaw + currentVel.z * cosineYaw;
		rightwardVel = currentVel.x * cosineYaw + currentVel.z * -negativeSineYaw;

		// Evaluate the direction the player is inputting
		forwardInput = (Math.abs(movementInput.z) < 0.1) ? 0.0 : movementInput.z * INPUT_FACTOR;
		rightwardInput = (Math.abs(movementInput.x) < 0.1) ? 0.0 : movementInput.x * INPUT_FACTOR;

		// Store y velocity to modify it separately from X and Z velocity
		yVel = currentVel.y;

		// Execute Mario's state behavior
		marioState.evaluateTransitions(MarioState.TransitionPhases.PRE_TICK);
		marioState.tick();
		marioState.evaluateTransitions(MarioState.TransitionPhases.POST_TICK);

		// Apply new y velocity
		Vec3d newVel = player.getVelocity();
		player.setVelocity(newVel.x, yVel, newVel.z);

		// Use velocities
		player.move(MovementType.SELF, player.getVelocity());
		marioState.evaluateTransitions(MarioState.TransitionPhases.POST_MOVE);
		marioState.evaluateTransitions(MarioState.TransitionPhases.POST_STATE); // only for power-ups

		// Decrement timers
		jumpLandingTime--;
		doubleJumpLandingTime--;

		// Finish
		player.updateLimbs(false);
		return true;
	}

	public static void approachAngleAndAccel(
			double forwardAccel, double forwardTarget, double forwardAngleContribution,
			double strafeAccel, double strafeTarget, double strafeAngleContribution,
			double redirectDelta
	) {
		Vector2d redirectedVel;

//		ModMarioQuaMario.LOGGER.info("approachAngleAndAccel:"
//				+ "\n forwardAccel: " + forwardAccel
//				+ "\n forwardTarget: " + forwardTarget
//				+ "\n forwardAngleContribution: " + forwardAngleContribution
//				+ "\n strafeAccel: " + strafeAccel
//				+ "\n strafeTarget: " + strafeTarget
//				+ "\n strafeAngleContribution: " + strafeAngleContribution
//				+ "\n redirectDelta: " + redirectDelta
//		);

		if(redirectDelta == 0 || (forwardAngleContribution == 0 && strafeAngleContribution == 0) ||
				(MathHelper.approximatelyEquals(forwardVel, 0) && MathHelper.approximatelyEquals(rightwardVel, 0) )) {
			redirectedVel = new Vector2d(forwardVel, rightwardVel);
		}
		else {
			Vector2d currentVel = new Vector2d(forwardVel, rightwardVel);
			Vector2d intendedAngle = new Vector2d(forwardAngleContribution, strafeAngleContribution);

			if(redirectDelta > 0) redirectedVel = slerp(currentVel, intendedAngle, redirectDelta);
			else redirectedVel = intendedAngle.normalize(currentVel.length()); // redirectAngle < 0 for instant redirection
		}

		Vector2d newVel;
		if(forwardAccel == 0 && strafeAccel == 0) {
			// If we're only redirecting then we're done here, no need to calculate acceleration & apply speed cap
			newVel = redirectedVel;
		}
		else {
			// Ensure forwardAccel and strafeAccel are positive
			forwardAccel = Math.abs(forwardAccel);
			strafeAccel = Math.abs(strafeAccel);

			// Calculate which way to accelerate
			double forwardAccelDir, strafeAccelDir;
			double forwardDifference = forwardTarget - redirectedVel.x;
			if(MathHelper.approximatelyEquals(forwardDifference, 0))
				forwardAccelDir = 0;
			else if(forwardAccel < Math.abs(forwardDifference))
				forwardAccelDir = Math.signum(forwardDifference);
			else {
				forwardAccelDir = 0;
				redirectedVel.x = forwardTarget;
			}
			double strafeDifference = strafeTarget - redirectedVel.y;
			if(MathHelper.approximatelyEquals(strafeDifference, 0))
				strafeAccelDir = 0;
			else if(strafeAccel < Math.abs(strafeDifference))
				strafeAccelDir = Math.signum(strafeDifference);
			else {
				strafeAccelDir = 0;
				redirectedVel.y = strafeTarget;
			}

			// Calculate the acceleration vector and normalize it, so the player won't get extra acceleration by strafing
			Vector2d accelVector = new Vector2d(
					forwardAccel * forwardAccelDir,
					strafeAccel * strafeAccelDir
			);
			if(accelVector.x != 0 || accelVector.y != 0) {
				double accelVectorMaxLength = Math.max(forwardAccel, strafeAccel);
				if(accelVector.lengthSquared() > accelVectorMaxLength * accelVectorMaxLength)
					accelVector.normalize(accelVectorMaxLength);
			}

			// Calculate the new velocity
			newVel = new Vector2d(
					redirectedVel.x + accelVector.x,
					redirectedVel.y + accelVector.y
			);

			// Calculate & apply soft speed cap
			double speedCap = Math.max(Math.abs(forwardTarget), Math.abs(strafeTarget));
			double speedCapSquared = speedCap * speedCap;
			double oldVelLengthSquared = Vector2d.lengthSquared(forwardVel, rightwardVel);

			if(newVel.lengthSquared() > oldVelLengthSquared) {
				if(oldVelLengthSquared > speedCapSquared)
					newVel.normalize(Vector2d.length(forwardVel, rightwardVel));
				else if(newVel.lengthSquared() > speedCapSquared)
					newVel.normalize(speedCap);
			}
		}

		// Assign velocities
		assignForwardStrafeVelocities(newVel.x, newVel.y);
	}

	// Function to perform SLERP on 2D vectors
	private static Vector2d slerp(Vector2d currentVelocity, Vector2d intendedAngle, double turnSpeedDegrees) {
		// Convert turnSpeed to radians
		double turnSpeedRadians = Math.toRadians(turnSpeedDegrees);

		// Normalize the input vectors (slerp typically operates on normalized vectors)
		Vector2d currentDir = new Vector2d(currentVelocity).normalize();
		Vector2d intendedDir = new Vector2d(intendedAngle).normalize();

		// Calculate the angle between the two vectors using the dot product
		double dotProduct = currentDir.dot(intendedDir);
		// Clamp the dot product to ensure it's within the valid range for acos [-1, 1]
		dotProduct = MathHelper.clamp(dotProduct, 0.0, 1.0);

		// Calculate the angle between the vectors
		double angleBetween = Math.acos(dotProduct);

		// If the angle is very small, just return the current velocity (no need to slerp)
		if(angleBetween < MathHelper.EPSILON || MathHelper.approximatelyEquals(angleBetween, MathHelper.PI))
			return new Vector2d(currentVelocity);

		// Calculate the fraction of the way we want to rotate (clamp to 0.0 to 1.0)
		double t = Math.min(1.0, turnSpeedRadians / angleBetween);

		// Slerp calculation
		double sinTotal = Math.sin(angleBetween);
		double ratioA = Math.sin((1 - t) * angleBetween) / sinTotal;
		double ratioB = Math.sin(t * angleBetween) / sinTotal;

		// Compute the new direction as a weighted sum of the two directions
		Vector2d newDir = new Vector2d(
				currentDir.x * ratioA + intendedDir.x * ratioB,
				currentDir.y * ratioA + intendedDir.y * ratioB
		);

		// Maintain the original magnitude of the velocity
		newDir.mul(currentVelocity.length());

		return newDir; // Return the interpolated direction with original magnitude
	}

	public static void assignForwardStrafeVelocities(double forward, double strafe) {
		// Calculate forward and sideways vector components
		double yawRad = Math.toRadians(player.getYaw());
		double negativeSineYaw = -Math.sin(yawRad);
		double cosineYaw = Math.cos(yawRad);

		/*
		 Apply hard caps to ensure Mario can't accelerate WAY too fast
		 The limit is set to be about the top speed of a boat on normal ice.
		 Backwards speed has a much higher limit than forwards speed because I'm a sucker for SM64 TASes.
		*/
		forward = MathHelper.clamp(forward, -3.75, 2.1);
		strafe = MathHelper.clamp(strafe, -1.9, 1.9);

		player.setVelocity(forward * negativeSineYaw + strafe * cosineYaw, yVel, forward * cosineYaw + strafe * -negativeSineYaw);
	}

	public static void groundAccel(
			CharaStat accelStat, CharaStat speedStat, double forwardAngleContribution,
			CharaStat strafeAccelStat, CharaStat strafeSpeedStat, double strafeAngleContribution,
			CharaStat redirectStat
	) {
		// Get slipperiness
		double slipFactor = getSlipFactor();

		approachAngleAndAccel(
				accelStat.getValue() * slipFactor, speedStat.getValue() * forwardInput, forwardAngleContribution * forwardInput,
				strafeAccelStat.getValue() * slipFactor, strafeSpeedStat.getValue() * rightwardInput, strafeAngleContribution * rightwardInput,
				redirectStat.getValue() * slipFactor
		);
	}

	public static void airborneAccel(
			CharaStat accelStat, CharaStat speedStat, double forwardAngleContribution,
			CharaStat strafeAccelStat, CharaStat strafeSpeedStat, double strafeAngleContribution,
			CharaStat redirectStat
	) {
		// Unlike when on the ground, when Mario is in midair, neutral inputs don't cause him to accelerate towards 0.
		// He only accelerates when actively making an input, and will always try to accelerate in that direction, never backwards.
		double accelValue, strafeAccelValue;
		if(forwardInput != 0 && (Math.signum(forwardVel) != Math.signum(forwardInput) || Math.abs(forwardVel) < Math.abs(speedStat.getValue())))
			accelValue = accelStat.getValue() * forwardInput;
		else accelValue = 0;

		if(rightwardInput != 0 && (Math.signum(rightwardVel) != Math.signum(rightwardInput) || Math.abs(rightwardVel) < Math.abs(strafeSpeedStat.getValue())))
			strafeAccelValue = strafeAccelStat.getValue() * rightwardInput;
		else strafeAccelValue = 0;

		approachAngleAndAccel(
				accelValue, speedStat.getValue() * Math.signum(forwardInput), forwardAngleContribution * forwardInput,
				strafeAccelValue, strafeSpeedStat.getValue() * Math.signum(rightwardInput), strafeAngleContribution * rightwardInput,
				redirectStat.getValue()
		);

	}

	public static void applyDrag(CharaStat dragStat, CharaStat dragMinStat, CharaStat redirectionStat, double forwardAngleContribution, double strafeAngleContribution) {
		double dragStatValue = dragStat.getValue();
		boolean dragInverted = dragStatValue < 0;
		double dragAdjusted = dragStatValue * (dragInverted ? 1.0 : getSlipFactor());

		Vector2d deltaVelocities = new Vector2d(
				-dragAdjusted * forwardVel,
				-dragAdjusted * rightwardVel
		);

		double minDrag = dragMinStat.getValue() * getSlipFactor();
		double dragVelocity = deltaVelocities.lengthSquared();
		if(dragVelocity != 0 && dragVelocity < minDrag * minDrag)
			deltaVelocities.normalize(minDrag);

//		ModMarioQuaMario.LOGGER.info("applyDrag:"
//				+ "\n dragStat: " + dragStat
//				+ "\n dragStatValue: " + dragStatValue
//				+ "\n dragAdjusted: " + dragAdjusted
//				+ "\n minDrag: " + minDrag
//				+ "\n deltaVelocities 2: " + deltaVelocities
//		);

		if(dragInverted) {
			assignForwardStrafeVelocities(forwardVel + deltaVelocities.x, rightwardVel + deltaVelocities.y);
			approachAngleAndAccel(
					0,
					0,
					forwardAngleContribution * forwardInput,
					0,
					0,
					strafeAngleContribution * rightwardInput,
					redirectionStat.getValue()
			);
		}
		else
			approachAngleAndAccel(
					deltaVelocities.x,
					0,
					forwardAngleContribution * forwardInput,
					deltaVelocities.y,
					0,
					strafeAngleContribution * rightwardInput,
					redirectionStat.getValue()
			);
	}

	public static void applyDrag(CharaStat dragStat, CharaStat dragMinStat) {
		applyDrag(dragStat, dragMinStat, CharaStat.ZERO, 0, 0);
	}

	public static double getSlipFactor() {
		return Math.pow(0.6 / getFloorSlipperiness(), 3);
	}
	private static float getFloorSlipperiness() {
		if(player.isOnGround()) {
			BlockPos blockPos = player.getVelocityAffectingPos();
			return player.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
		}
		return 0.6F;
	}

	public static double getStatBuffer(CharaStat stat) {
		return stat.getValue() * 1.015;
	}

	public static double getStatThreshold(CharaStat stat) {
		return stat.getValue() * 0.99;
	}
}