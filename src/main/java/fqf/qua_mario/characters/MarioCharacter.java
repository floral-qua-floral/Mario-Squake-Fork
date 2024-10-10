package fqf.qua_mario.characters;

import fqf.qua_mario.MarioRegistries;
import fqf.qua_mario.powerups.PowerUp;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public abstract class MarioCharacter {
	protected Identifier ID;
	protected String name;
	private final Map<PowerUp, String> MODELS = new HashMap<>();
	protected EnumMap<CharaStat, Double> statFactors;

	public double getStatFactor(CharaStat stat) {
		return this.statFactors.getOrDefault(stat, 1.0);
	}

	public Identifier getID() {
		return(this.ID);
	}
	public String getName() {
		return(this.name);
	}
	public String getModel(PowerUp powerUp) {
		return(this.MODELS.get(powerUp));
	}
	public void register() {
		Registry.register(MarioRegistries.CHARACTERS, this.getID(), this);
		for(PowerUp addPowerModel : MarioRegistries.POWER_UPS) {
			MODELS.put(addPowerModel, addPowerModel.getFormName(this));
		}
	}
}
