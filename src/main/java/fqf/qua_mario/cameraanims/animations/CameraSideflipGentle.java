package fqf.qua_mario.cameraanims.animations;

import fqf.qua_mario.cameraanims.CameraAnim;

public class CameraSideflipGentle extends CameraAnim {
	public static final CameraSideflipGentle INSTANCE = new CameraSideflipGentle();

	public static double deltaPitch = 180;

	private CameraSideflipGentle() {
		this.name = "Sideflip (Gentle)";
		this.duration = 13.5;
	}

	@Override
	public double[] getRotations(double progress) {
		return new double[]{0.0, cappedLerp(progress, deltaPitch, 0), cappedLerp(progress, 0, 180)};
	}
}
