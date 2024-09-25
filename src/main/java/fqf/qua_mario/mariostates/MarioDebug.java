package fqf.qua_mario.mariostates;

import fqf.qua_mario.MarioClient;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioDebug extends MarioState {
	public static final MarioDebug INSTANCE = new MarioDebug();

	private MarioDebug() {
		this.name = "Debug";
	}

	@Override
	public void tick() {
		MarioClient.yVel = 0;

		MarioClient.accelerate(
				MarioClient.forwardInput * 0.5, MarioClient.rightwardInput * 0.5,
				0.04, 0.04,
				1, 1,
				0.04, -0.04);
	}
}
