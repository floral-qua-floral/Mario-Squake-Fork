package squeek.quakemovement.mixin;

import net.minecraft.client.render.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
	
	@Final @Shadow private Quaternionf rotation;
	@Final @Shadow private static Vector3f HORIZONTAL;
	@Final @Shadow private static Vector3f VERTICAL;
	@Final @Shadow private static Vector3f DIAGONAL;
	@Final @Shadow private Vector3f horizontalPlane;
	@Final @Shadow private Vector3f verticalPlane;
	@Final @Shadow private Vector3f diagonalPlane;

	@Shadow private boolean thirdPerson;

	@Inject(method = "setRotation", at = @At(value = "INVOKE", target =
		"Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"
		), require = 1, cancellable = true)
	protected void setRotation(float yaw, float pitch, CallbackInfo ci) {

		if(this.thirdPerson) {
			return;
		}

		float roll = (float) Math.toRadians(0);
//		if (!CoolElytraClient.isFrontView) roll = -roll;

		this.rotation.rotationYXZ((float)(Math.PI - Math.toRadians(yaw)), (float)Math.toRadians(-pitch), roll);
        HORIZONTAL.rotate(this.rotation, this.horizontalPlane);
        VERTICAL.rotate(this.rotation, this.verticalPlane);
        DIAGONAL.rotate(this.rotation, this.diagonalPlane);
		ci.cancel();
	}
	
}
