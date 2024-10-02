package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.FireForm;
import fqf.qua_mario.powerups.forms.SmallForm;
import fqf.qua_mario.powerups.forms.SuperForm;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class MarioRegistries {
	public static final RegistryKey<Registry<MarioCharacter>> CHARACTERS_KEY = RegistryKey.ofRegistry(
			Identifier. of(ModMarioQuaMario.MOD_ID, "characters"));
	public static final Registry<MarioCharacter> CHARACTERS = FabricRegistryBuilder.createSimple(CHARACTERS_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final RegistryKey<Registry<PowerUp>> POWER_UPS_KEY = RegistryKey.ofRegistry(
			Identifier. of(ModMarioQuaMario.MOD_ID, "power_ups"));
	public static final Registry<PowerUp> POWER_UPS = FabricRegistryBuilder.createSimple(POWER_UPS_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static void register() {
		registerCharacters();
		registerPowerUps();
		registerStompTypes();
	}

	public static void registerCharacters() {
		CharaMario.INSTANCE.register();
	}
	public static void registerPowerUps() {
		SmallForm.INSTANCE.register();
		SuperForm.INSTANCE.register();
		FireForm.INSTANCE.register();
	}
	public static void registerStompTypes() {

	}
}
