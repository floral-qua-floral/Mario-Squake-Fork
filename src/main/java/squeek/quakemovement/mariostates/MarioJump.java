package squeek.quakemovement.mariostates;

import squeek.quakemovement.MarioClient;
import squeek.quakemovement.MarioInputs;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioJump extends MarioState {
	public static final MarioJump INSTANCE = new MarioJump();

	private MarioJump() {
		this.name = "Jump";

		preTickTransitions = new ArrayList<MarioStateTransition>(Arrays.asList(new MarioStateTransition[]{
				CommonTransitions.LANDING,
		}));
	}

	@Override
	public void tick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
		applyGravity();

		final double CAP_SPEED = 0.275;
		if((MarioInputs.isHeld(MarioInputs.Key.SNEAK) || !MarioInputs.isHeld(MarioInputs.Key.JUMP)) && MarioClient.yVel > CAP_SPEED) {
			MarioClient.yVel = CAP_SPEED;
		}
	}
}
