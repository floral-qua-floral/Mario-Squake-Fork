package squeek.quakemovement.mariostates;

import squeek.quakemovement.MarioClient;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioAerial extends MarioState {
	public static final MarioAerial INSTANCE = new MarioAerial();

	private MarioAerial() {
		this.name = "Aerial";

		preTickTransitions = new ArrayList<MarioStateTransition>(Arrays.asList(new MarioStateTransition[]{
				CommonTransitions.LANDING,
		}));
	}

	@Override
	public void tick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
		applyGravity();
	}
}
