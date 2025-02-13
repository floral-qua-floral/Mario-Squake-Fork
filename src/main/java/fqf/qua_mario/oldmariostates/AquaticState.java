package fqf.qua_mario.oldmariostates;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.states.airborne.Aerial;
import fqf.qua_mario.stomptypes.StompType;
import net.minecraft.registry.tag.FluidTags;
import org.jetbrains.annotations.Nullable;

public abstract class AquaticState extends OldMarioState {
	@Nullable protected StompType stompType;

	protected abstract void waterTick();

	@Override
	public boolean getSneakLegality() {
		return false;
	}

	@Override
	public void tick() {
		waterTick();
		applyGravity();

		if(stompType != null && MarioClient.yVel < 0) {
			stompType.attemptStomp();
		}
	}

	protected void applyGravity() {
		applyGravity(CharaStat.WATER_GRAVITY.getValue(), CharaStat.WATER_TERMINAL_VELOCITY.getValue());
	}
	protected void applyGravity(double accel, double terminalVelocity) {
		if(MarioClient.yVel > terminalVelocity)
			MarioClient.yVel = Math.max(terminalVelocity, MarioClient.yVel + accel);
	}

	public record AquaticTransitions() {
		public static final MarioStateTransition EXIT_WATER = () -> {
			if(MarioClient.player.getFluidHeight(FluidTags.WATER) < 0.4) {
				MarioClient.yVel = Math.max(MarioClient.yVel, 0.65);
				return Aerial.INSTANCE;
			}
			return null;
		};
	}
}
