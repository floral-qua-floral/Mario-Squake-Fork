package fqf.qua_mario.oldmariostates.states.airborne;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.oldmariostates.AirborneState;

import java.util.ArrayList;
import java.util.Arrays;

public class LavaBoost extends AirborneState {
	public static final LavaBoost INSTANCE = new LavaBoost();

	private LavaBoost() {
		this.name = "Lava Boost";
		this.isJump = false;
		this.jumpCapStat = null;
		this.stompType = null;

		preTickTransitions = new ArrayList<>(Arrays.asList(
				() -> {
					if(MarioClient.yVel > -0.03)
						return AirborneTransitions.LANDING.evaluate();
					return null;
				}
		));
	}

	@Override
	public void airTick() {
		ModMarioQuaMario.LOGGER.info("Lava boost airTick?");

		CharaStat driftAccelStat = MarioClient.yVel > 0 ? CharaStat.LAVA_BOOST_RISING_DRIFT_ACCEL : CharaStat.LAVA_BOOST_FALLING_DRIFT_ACCEL;
		aerialDrift(
				driftAccelStat, CharaStat.LAVA_BOOST_DRIFT_SPEED,
				driftAccelStat, CharaStat.LAVA_BOOST_DRIFT_SPEED,
				driftAccelStat, CharaStat.LAVA_BOOST_DRIFT_SPEED,
				CharaStat.LAVA_BOOST_REDIRECTION
		);

		// Bouncing
		if(MarioClient.player.isOnGround() && MarioClient.yVel < 0)
			MarioClient.yVel *= -0.7;
	}
}
