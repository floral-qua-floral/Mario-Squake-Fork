package fqf.qua_mario.characters;

import com.google.common.collect.Lists;
import fqf.qua_mario.VoiceLine;
import fqf.qua_mario.characters.characters.CharaMario;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public abstract class MarioCharacter {
	public static final List<MarioCharacter> CHILDREN = Lists.newArrayList();

	protected Identifier ID;
	protected String name;

	public Identifier getID() {
		return(this.ID);
	}
	public String getName() {
		return(this.name);
	}

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
}
