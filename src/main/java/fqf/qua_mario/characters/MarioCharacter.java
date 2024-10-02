package fqf.qua_mario.characters;

import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.ModMarioQuaMario;
import fqf.qua_mario.VoiceLine;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public abstract class MarioCharacter {
	protected Identifier ID;
	protected String name;

	@Nullable
	public abstract String getSoundPrefix();

	protected EnumMap<CharaStat, Double> statFactors;

	public double getStatValue(CharaStat stat) {
		return stat.getDefaultValue() * statFactors.getOrDefault(stat, 1.0);
	}

	public void playVoiceLine(VoiceLine line) {
		if(getSoundPrefix() == null) return;

		// play sfx
	}

	public Identifier getID() {
		return(this.ID);
	}
	public String getName() {
		return(this.name);
	}
	public void register() {
		Registry.register(MarioRegistries.CHARACTERS, getID(), this);
	}
}
