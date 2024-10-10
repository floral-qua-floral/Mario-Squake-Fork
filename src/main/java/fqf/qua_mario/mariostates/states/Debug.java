package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.GroundedState;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.mariostates.states.groundbound.DuckWaddle;
import fqf.qua_mario.mariostates.states.groundbound.Grounded;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Debug extends MarioState {
	public static final Debug INSTANCE = new Debug();

	private Debug() {
		this.name = "Debug";

		postMoveTransitions = new ArrayList<>(List.of(
				() -> {
					if(Input.SPIN.isPressed())
						return Grounded.INSTANCE;
					return null;
				}
		));
	}

	@Override
	public void tick() {
		MarioClient.yVel = Input.JUMP.isHeld() ? 0.7 : (Input.DUCK.isHeld() ? -0.7 : 0.0);

		MarioClient.airborneAccel(
				MarioClient.forwardInput >= 0 ? CharaStat.DRIFT_FORWARD_ACCEL : CharaStat.DRIFT_BACKWARD_ACCEL,
				MarioClient.forwardInput >= 0 ? CharaStat.DRIFT_FORWARD_SPEED : CharaStat.DRIFT_BACKWARD_SPEED,
				1.0,
				CharaStat.DRIFT_SIDE_ACCEL,
				CharaStat.DRIFT_SIDE_SPEED,
				1.0,
				CharaStat.DRIFT_REDIRECTION
		);
	}
}
