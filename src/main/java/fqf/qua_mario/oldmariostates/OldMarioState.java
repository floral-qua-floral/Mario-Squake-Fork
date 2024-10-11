package fqf.qua_mario.oldmariostates;

import fqf.qua_mario.*;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.states.airborne.*;
import fqf.qua_mario.oldmariostates.states.aquatic.WaterTread;
import net.minecraft.entity.MovementType;
import net.minecraft.registry.tag.FluidTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class OldMarioState {
	@FunctionalInterface
	public interface MarioStateTransition {
		@Nullable OldMarioState evaluate();
	}

	protected String name;
	protected ArrayList<MarioStateTransition> preTickTransitions;
	protected ArrayList<MarioStateTransition> postTickTransitions;
	protected ArrayList<MarioStateTransition> postMoveTransitions = new ArrayList<>(Arrays.asList(
			CommonTransitions.LAVA_BOOST,
			CommonTransitions.ENTER_WATER
	));

	public enum TransitionPhases {
		PRE_TICK,
		POST_TICK,
		POST_MOVE,
		POST_STATE;

		private ArrayList<MarioStateTransition> getTransitionList(OldMarioState state) {
			return switch(this) {
				case PRE_TICK -> state.preTickTransitions;
				case POST_TICK -> state.postTickTransitions;
				case POST_MOVE -> state.postMoveTransitions;
				case POST_STATE -> null;
			};
		}
	}

	public String getName() {
		return name;
	}
	public boolean getSneakLegality() {
		return true;
	}

	public void evaluateTransitions(TransitionPhases phase) {
		OldMarioState powerTransitionResult = MarioClient.powerUp.customTransition(this, phase);
		if(MarioClient.changeState(powerTransitionResult)) return;

		ArrayList<MarioStateTransition> transitionList = phase.getTransitionList(this);
		if(transitionList != null) for (MarioStateTransition transition : transitionList) {
			OldMarioState transitionResult = transition.evaluate();
			if(transitionResult != null && MarioClient.changeState(transitionResult.getTransitionTarget(MarioClient.getState())))
				return;
		}
	}

	public OldMarioState getTransitionTarget(OldMarioState from) {
		OldMarioState stateFromPowerUp = MarioClient.powerUp.interceptTransition(from, this);
		return stateFromPowerUp != null ? stateFromPowerUp : this;
	}

	public abstract void tick();



	public record CommonTransitions() {
		public static final MarioStateTransition ENTER_WATER = () -> {
			if(MarioClient.player.getFluidHeight(FluidTags.WATER) > 0.5) {
				return WaterTread.INSTANCE;
			}
			return null;
		};

		public static final MarioStateTransition LAVA_BOOST = () -> {
			if(MarioClient.player.isInLava()) {
				if(MarioClient.getState() != LavaBoost.INSTANCE)
					MarioClient.player.move(MovementType.SELF, MarioClient.player.getVelocity().multiply(-5));
				MarioClient.player.setVelocity(0.0, CharaStat.LAVA_BOOST_VEL.getValue(), 0.0);
				VoiceLine.BURNT.broadcast();
				return LavaBoost.INSTANCE;
			}
			return null;
		};
	}
}

