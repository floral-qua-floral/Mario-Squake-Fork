package squeek.quakemovement.cameraanims;

import net.minecraft.util.math.MathHelper;
import squeek.quakemovement.mariostates.MarioAerial;
import squeek.quakemovement.mariostates.MarioState;

import java.util.ArrayList;
import java.util.Arrays;

public class CameraSideflip extends CameraAnim {
	public static final CameraSideflip INSTANCE = new CameraSideflip();

	public static float deltaPitch;

	private CameraSideflip() {
		this.name = "Sideflip";
		this.duration = 45;
	}

	@Override
	public double[] getRotations(double progress) {
		return new double[]{0.0, MathHelper.lerp(progress, deltaPitch, 0), MathHelper.lerp(progress, 180, 0)};
	}
}
