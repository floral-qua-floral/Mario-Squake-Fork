package fqf.qua_mario.cameraanims;

public class CameraSideflipNoFunAllowed extends CameraAnim {
	public static final CameraSideflipNoFunAllowed INSTANCE = new CameraSideflipNoFunAllowed();

	private CameraSideflipNoFunAllowed() {
		this.name = "Sideflip (No Fun Allowed)";
		this.duration = 8;
	}

	@Override
	public double[] getRotations(double progress) {
		return new double[]{cappedLerp(progress, 180, 0), 0.0, 0.0};
	}
}
