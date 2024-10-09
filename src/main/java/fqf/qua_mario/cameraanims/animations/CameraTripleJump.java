package fqf.qua_mario.cameraanims.animations;

import fqf.qua_mario.cameraanims.CameraAnim;

public class CameraTripleJump extends CameraAnim {
	public static final CameraTripleJump INSTANCE = new CameraTripleJump();

	private CameraTripleJump() {
		this.name = "Triple Jump";
		this.duration = 11;
	}

	@Override
	public double[] getRotations(double progress) {
		return new double[]{0.0, cappedLerp(progress, -360, 0), 0.0};
	}
}
