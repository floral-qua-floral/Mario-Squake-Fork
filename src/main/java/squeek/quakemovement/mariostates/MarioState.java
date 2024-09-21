package squeek.quakemovement.mariostates;

import org.slf4j.Logger;
import squeek.quakemovement.ModQuakeMovement;

import java.util.ArrayList;

public abstract class MarioState {
	@FunctionalInterface
	interface MarioStateTransition {
		MarioState evaluate();
	}

	protected static final Logger LOGGER = ModQuakeMovement.LOGGER;

	public String name;
	public ArrayList<MarioStateTransition> transitions;

//	public MarioState(MarioStateTransition... transitions) {
//		this.transitions = transitions;
//	}

	public MarioState evaluateTransitions() {
		for (MarioStateTransition transition : transitions) {
			MarioState nextState = transition.evaluate();
			if (nextState != null) {
				return nextState;
			}
		}
		return this; // Stay in the same state if no transition occurs
	}


	abstract void tick();
}

