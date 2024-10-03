package fqf.qua_mario.mariostates.states;

import fqf.qua_mario.Input;
import fqf.qua_mario.MarioClient;
import fqf.qua_mario.MarioInputs;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;

import java.util.ArrayList;
import java.util.Arrays;

public class Jump extends MarioState {
	public static final Jump INSTANCE = new Jump();

	private Jump() {
		this.name = "Jump";

		preTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					MarioState landingResult = CommonTransitions.LANDING.evaluate();
					if(landingResult != null) {
						MarioClient.jumpLandingTime = 6;
						return landingResult;
					}
					return null;
				},
				() -> {
					if(MarioInputs.isPressed(MarioInputs.Key.SNEAK)) {
						// Initiate ground pound
					}
					return null;
				}
		));
	}

	@Override
	public void tick() {
		MarioClient.aerialAccel(MarioClient.forwardInput * 0.04, MarioClient.rightwardInput * 0.04, 0.25, -0.25, 0.195);
//		applyGravity();

		final double CAP_SPEED = MarioClient.getStat(CharaStat.JUMP_CAP);
//		if((MarioInputs.isHeld(MarioInputs.Key.SNEAK) || !MarioInputs.isHeld(MarioInputs.Key.JUMP)) && MarioClient.yVel > CAP_SPEED) {
//			MarioClient.yVel = CAP_SPEED;
//		}

		if(MarioClient.yVel > CAP_SPEED && !Input.JUMP.isHeld()) MarioClient.yVel = CAP_SPEED;

		if(MarioClient.yVel > (Input.JUMP.isHeld() ? 0 : CAP_SPEED))
			applyGravity(CharaStat.JUMP_GRAVITY);
		else
			applyGravity(CharaStat.GRAVITY);

		StompBasic.INSTANCE.attemptStomp();

//		Animati
	}
}
