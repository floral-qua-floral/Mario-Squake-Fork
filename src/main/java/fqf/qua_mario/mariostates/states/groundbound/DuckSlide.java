package fqf.qua_mario.mariostates.states.groundbound;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.AirborneState;
import fqf.qua_mario.mariostates.GroundedState;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.mariostates.states.airborne.DuckJump;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class DuckSlide extends GroundedState {
	public static final DuckSlide INSTANCE = new DuckSlide();

	public static boolean enterDuckSlide() {
		double threshold = CharaStat.DUCK_SLIDE_THRESHOLD.getValue();
		return Vector2d.lengthSquared(MarioClient.forwardVel, MarioClient.rightwardVel) > threshold * threshold;
	}

	private DuckSlide() {
		this.name = "Duck Slide";

		preTickTransitions = new ArrayList<>(List.of(
				GroundedTransitions.FALL,
				() -> {
					// Release duck
					if(!Input.DUCK.isHeld()) {
						return Grounded.INSTANCE;
					}
					return null;
				},
				() -> {
					// Transition to DuckWaddle
					if(MathHelper.approximatelyEquals(Vector2d.lengthSquared(MarioClient.forwardVel, MarioClient.rightwardVel), 0)) {
						return DuckWaddle.INSTANCE;
					}
					return null;
				}
		));

		postTickTransitions = new ArrayList<>(List.of(
//				DuckWaddle.BACKFLIP,
				() -> {
					if(Input.JUMP.isPressed()) {
						GroundedTransitions.performJump(CharaStat.DUCK_JUMP_VELOCITY, null);
						VoiceLine.DUCK_JUMP.broadcast();
						return DuckJump.INSTANCE;
					}
					return null;
				}
		));
	}

	@Override
	public void groundedTick() {
		MarioClient.applyDrag(CharaStat.DUCK_SLIDE_DRAG, CharaStat.DUCK_SLIDE_DRAG,
				CharaStat.DUCK_SLIDE_REDIRECTION, 1, 1);
	}
}
