package fqf.qua_mario.characters;

import fqf.qua_mario.MarioClient;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public abstract class Character {

	protected String name;
	protected String fullName;

	public String getName(boolean full) {
		return(full ? fullName : name);
	}

	@Nullable
	public abstract String getSoundPrefix();

	protected EnumMap<CharaStat, Double> statFactors;

	public double getStatValue(CharaStat stat) {
		return stat.getDefaultValue() * statFactors.getOrDefault(stat, 1.0);
	}
}
