package squeek.quakemovement.cameraanims;

import net.minecraft.entity.MovementType;
import net.minecraft.registry.tag.FluidTags;
import org.slf4j.Logger;
import squeek.quakemovement.MarioClient;
import squeek.quakemovement.MarioInputs;
import squeek.quakemovement.ModQuakeMovement;
import squeek.quakemovement.mariostates.MarioAerial;
import squeek.quakemovement.mariostates.MarioGrounded;
import squeek.quakemovement.mariostates.MarioJump;

import java.util.ArrayList;

public abstract class CameraAnim {
	@FunctionalInterface
	protected interface MarioStateTransition {
		CameraAnim evaluate();
	}

	public String name;
	public int duration;

	public abstract double[] getRotations(double progress);

//	public static void tick;
}

