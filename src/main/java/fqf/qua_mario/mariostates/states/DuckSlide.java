package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class DuckSlide extends MarioState {
	public static final DuckSlide INSTANCE = new DuckSlide();

	public static boolean enterDuckSlide() {
		double threshold = CharaStat.DUCK_SLIDE_THRESHOLD.getValue();
		return Vector2d.lengthSquared(MarioClient.forwardVel, MarioClient.rightwardVel) > threshold * threshold;
	}

	private DuckSlide() {
		this.name = "Duck Slide";

		preTickTransitions = new ArrayList<>(List.of(
				CommonTransitions.FALL,
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
				DuckWaddle.BACKFLIP,
				() -> {
					if(Input.JUMP.isPressed()) {
						CommonTransitions.performJump(CharaStat.DUCK_JUMP_VELOCITY, null);
						VoiceLine.DUCK_JUMP.broadcast();
						return DuckJump.INSTANCE;
					}
					return null;
				}
		));
	}

	@Override
	public void tick() {
		MarioClient.applyDrag(CharaStat.DUCK_SLIDE_DRAG, CharaStat.DUCK_SLIDE_DRAG,
				CharaStat.DUCK_SLIDE_REDIRECTION, 1, 1);

		MarioClient.yVel = -0.1;
	}
}
