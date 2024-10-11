package fqf.qua_mario.powerups;

import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.oldmariostates.OldMarioState;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public abstract class PowerUp {
	protected Identifier ID;
	protected String prefix;
	protected float widthFactor = 1.0F;
	protected float heightFactor = 1.0F;
	protected float voicePitch = 1.0F;

	public abstract OldMarioState customTransition(OldMarioState state, OldMarioState.TransitionPhases phase);
	public abstract OldMarioState interceptTransition(OldMarioState from, OldMarioState to);

	public Identifier getID() {
		return(this.ID);
	}
	public String getPrefix() {
		return(this.prefix);
	}
	public float getWidthFactor() {
		return(this.widthFactor);
	}
	public float getHeightFactor() {
		return(this.heightFactor);
	}
	public float getVoicePitch() {
		return this.voicePitch;
	}
	public String getFormName(MarioCharacter character) {
		return this.getPrefix() + character.getName();
	}
	public void register() {
		Registry.register(MarioRegistries.POWER_UPS, this.getID(), this);
	}
}
