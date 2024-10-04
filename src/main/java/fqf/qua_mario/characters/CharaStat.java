package fqf.qua_mario.characters;

import fqf.qua_mario.MarioClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;

public enum CharaStat {
	WALK_ACCEL(0.045),
	WALK_STANDSTILL_ACCEL(0.125),
	WALK_STANDSTILL_THRESHOLD(0.12),
	WALK_SPEED(0.275),
	WALK_REDIRECTION(0),
	OVERWALK_ACCEL(0.02),

	IDLE_DEACCEL(0.075),

	BACKPEDAL_ACCEL(0.055),
	BACKPEDAL_SPEED(0.225),
	BACKPEDAL_REDIRECTION(0),
	OVERBACKPEDAL_ACCEL(0.04),

	SKID_THRESHOLD(0.285),
	SKID_FACTOR(0.9),

	RUN_ACCEL(0.0185),
	RUN_SPEED(0.5),
	RUN_REDIRECTION(2.75),
	OVERRUN_ACCEL(0.0175),

	P_SPEED(0.525),
	P_SPEED_REDIRECTION(3.4),

	STRAFE_ACCEL(0.065),
	STRAFE_SPEED(0.275),

	GRAVITY(-0.115),
	JUMP_GRAVITY(-0.095),
	TERMINAL_VELOCITY(-3.25),

	DRIFT_FORWARD_ACCEL,
	DRIFT_FORWARD_SPEED,
	DRIFT_SIDE_ACCEL,
	DRIFT_SIDE_SPEED,
	DRIFT_BACKWARD_ACCEL,
	DRIFT_BACKWARD_SPEED,
	DRIFT_REDIRECTION,

	JUMP_VELOCITY(0.86),
	JUMP_VELOCITY_ADDEND(0.165),
	JUMP_CAP(0.275),

	SIDEFLIP_VELOCITY,
	SIDEFLIP_CAP,
	SIDEFLIP_THRESHOLD(0.125),
	SIDEFLIP_BACKWARD_SPEED,

	CROUCH_JUMP_HEIGHT,
	CROUCH_JUMP_VELOCITY,
	CROUCH_JUMP_CAP,

	DOUBLE_JUMP_HEIGHT,
	DOUBLE_JUMP_HEIGHT_ADDEND,
	DOUBLE_JUMP_VELOCITY,
	DOUBLE_JUMP_VELOCITY_ADDEND,
	DOUBLE_JUMP_CAP,

	TRIPLE_JUMP_HEIGHT,
	TRIPLE_JUMP_VELOCITY,
	TRIPLE_JUMP_CAP,

	LONG_JUMP_HEIGHT,
	LONG_JUMP_VELOCITY,
	LONG_JUMP_CAP,
	LONG_JUMP_SPEED_FACTOR,
	LONG_JUMP_SPEED_ADDEND,
	LONG_JUMP_SPEED_CAP,

	ZERO(0);

	private final double DEFAULT_VALUE;

	CharaStat() {
		this.DEFAULT_VALUE = 0;
	}
	CharaStat(double defaultValue) {
		this.DEFAULT_VALUE = defaultValue;
	}

	public double getDefaultValue() {
		return DEFAULT_VALUE;
	}

	@Environment(EnvType.CLIENT)
	public double getValue() {
		// Only use client-side!!!!!!!!!!
		if(MarioClient.useCharacterStats)
			return MarioClient.character.getStatValue(this);
		return getDefaultValue();
	}

	public double getValue(ServerPlayerEntity player) {

		return getDefaultValue();
	}
}
