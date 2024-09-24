package fqf.qua_mario.mixin;

import fqf.qua_mario.ModQuakeMovement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fqf.qua_mario.MarioClient;

@Mixin(Camera.class)
public abstract class CameraMixin {
	
	@Final @Shadow private Quaternionf rotation;
	@Final @Shadow private static Vector3f HORIZONTAL;
	@Final @Shadow private static Vector3f VERTICAL;
	@Final @Shadow private static Vector3f DIAGONAL;
	@Final @Shadow private Vector3f horizontalPlane;
	@Final @Shadow private Vector3f verticalPlane;
	@Final @Shadow private Vector3f diagonalPlane;
	@Final @Shadow private boolean thirdPerson;
	@Final @Shadow private float lastTickDelta;

	@Inject(method = "setRotation", at = @At(value = "INVOKE", target =
		"Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"
		), require = 1, cancellable = true)
	protected void setRotation(float yaw, float pitch, CallbackInfo ci) {
		if(MarioClient.marioCameraAnim == null) return;

		float rightNow = MarioClient.player.getWorld().getTime() + lastTickDelta;
		float timeSinceAnimStart = rightNow - MarioClient.animStartTime;

		MarioClient.cameraAnimTimer += MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
		if(rightNow > MarioClient.animEndTime) {
			MarioClient.marioCameraAnim = null;
			return;
		}

		if(thirdPerson) return;

//		double progress = Math.min(1.0, MarioClient.cameraAnimTimer / MarioClient.marioCameraAnim.duration);
		double progress = Math.min(1.0, timeSinceAnimStart / MarioClient.marioCameraAnim.duration);
		double[] rotations = MarioClient.marioCameraAnim.getRotations(progress);

		this.rotation.rotationYXZ(
				(float) (Math.PI - Math.toRadians(yaw + rotations[0])),
				(float) Math.toRadians((pitch + rotations[1]) * -1),
				(float) Math.toRadians(rotations[2]));

        HORIZONTAL.rotate(this.rotation, this.horizontalPlane);
        VERTICAL.rotate(this.rotation, this.verticalPlane);
        DIAGONAL.rotate(this.rotation, this.diagonalPlane);

		MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();

		ci.cancel();
	}
	
}
