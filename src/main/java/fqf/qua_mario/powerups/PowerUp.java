package fqf.qua_mario.powerups;

import com.google.common.collect.Lists;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.powerups.forms.SuperMario;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class PowerUp {
	public static final List<PowerUp> CHILDREN = Lists.newArrayList(SuperMario.INSTANCE);

	protected Identifier ID;
	protected String prefix;

	public Identifier getID() {
		return(this.ID);
	}
	public String getPrefix() {
		return(this.prefix);
	}
	public String getFormName(MarioCharacter character) {
		return this.prefix + " " + character.getName();
	}
}
