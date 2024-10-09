package fqf.qua_mario.characters;

import fqf.qua_mario.MarioClient;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.StatChangingPowerUp;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

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

	DUCK_SLIDE_THRESHOLD(0.25),
	DUCK_SLIDE_BOOST(-0.15),
	DUCK_SLIDE_DRAG(0.03),
	DUCK_SLIDE_DRAG_MIN(0.01),
	DUCK_SLIDE_REDIRECTION(4),

	WADDLE_ACCEL(0.06),
	WADDLE_SPEED(0.08),
	WADDLE_STRAFE_ACCEL(0.06),
	WADDLE_STRAFE_SPEED(0.06),
	WADDLE_BACKPEDAL_ACCEL(0.0725),
	WADDLE_BACKPEDAL_SPEED(0.06),
	WADDLE_REDIRECTION(0),

	DUCK_JUMP_VELOCITY(0.858),
	DUCK_JUMP_CAP(0.14),

	SKID_THRESHOLD(0.285),
	SKID_DRAG(0.185),
	SKID_DRAG_MIN(0.02),
	SKID_REDIRECTION(4.5),

	RUN_ACCEL(0.0155),
	RUN_SPEED(0.5),
	RUN_REDIRECTION(2.75),
	OVERRUN_ACCEL(0.0175),

	P_SPEED(0.5375),
	P_SPEED_REDIRECTION(3.4),

	STRAFE_ACCEL(0.065),
	STRAFE_SPEED(0.275),

	SKATE_SPEED(0.5),
	SKATE_REDIRECTION(1.2),

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

	JUMP_SPEED_LOSS(0.15),
	JUMP_VELOCITY(0.858),
	JUMP_VELOCITY_ADDEND(0.117),
	JUMP_CAP(0.39),

	SIDEFLIP_VELOCITY(1.065),
	SIDEFLIP_CAP(0.65),
	SIDEFLIP_THRESHOLD(0.2),
	SIDEFLIP_BACKWARD_SPEED(-0.375),

	ADVANCED_JUMP_THRESHOLD(0.34),

	DOUBLE_JUMP_VELOCITY(0.939),
	DOUBLE_JUMP_VELOCITY_ADDEND(0.08),
	DOUBLE_JUMP_CAP(0.285),

	TRIPLE_JUMP_VELOCITY(1.175),
	TRIPLE_JUMP_CAP(0.65),

	LONG_JUMP_VELOCITY,
	LONG_JUMP_CAP,
	LONG_JUMP_SPEED_FACTOR,
	LONG_JUMP_SPEED_ADDEND,
	LONG_JUMP_SPEED_CAP,

	LAVA_BOOST_VEL(1.0),
	LAVA_BOOST_REDIRECTION(7.5),

	STOMP_BASE_DAMAGE(4),
	STOMP_BASE_VELOCITY(0.95),
	STOMP_CAP(0.39),

	GROUND_POUND_VELOCITY,

	WATER_GRAVITY,
	WATER_TERMINAL_VELOCITY,
	WATER_DRAG,

	SWIM_UP_VEL,
	PADDLE_TERMINAL_VELOCITY,

	SWIM_ACCEL,
	SWIM_SPEED,
	SWIM_STRAFE_ACCEL,
	SWIM_STRAFE_SPEED,
	SWIM_BACKPEDAL_ACCEL,
	SWIM_BACKPEDAL_SPEED,

	AQUATIC_GROUND_POUND_VELOCITY,
	AQUATIC_GROUND_POUND_DRAG,

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
		return this.getValue(MarioClient.useCharacterStats, MarioClient.character, MarioClient.powerUp);
	}

	public double getValue(PlayerEntity player) {
		if(ModMarioQuaMario.playerIsMarioClient(player)) return this.getValue();
		return this.getValue(ModMarioQuaMario.getUseCharacterStats(player), ModMarioQuaMario.getCharacter(player), ModMarioQuaMario.getPowerUp(player));
	}

	public double getValue(boolean useCharacterStats, @NotNull MarioCharacter character, @NotNull PowerUp powerUp) {
		return(
				(useCharacterStats ? character.getStatFactor(this) : 1) *
				(powerUp instanceof StatChangingPowerUp statChangingPowerUp ? statChangingPowerUp.getStatFactor(this) : 1.0) *
				this.getDefaultValue()
		);
	}
}
