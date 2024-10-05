package fqf.qua_mario.powerups;

import fqf.qua_mario.characters.CharaStat;

import java.util.EnumMap;

public abstract class StatChangingPowerUp extends PowerUp {
	protected EnumMap<CharaStat, Double> statFactors;

	public double getStatFactor(CharaStat stat) {
		return this.statFactors.getOrDefault(stat, 1.0);
	}
}
