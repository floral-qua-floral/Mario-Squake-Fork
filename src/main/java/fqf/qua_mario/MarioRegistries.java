package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.characters.characters.Luigi;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.FireForm;
import fqf.qua_mario.powerups.forms.SmallForm;
import fqf.qua_mario.powerups.forms.SuperForm;
import fqf.qua_mario.stomptypes.StompType;
import fqf.qua_mario.stomptypes.stomptypes.StompBasic;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

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

	public static final RegistryKey<Registry<StompType>> STOMP_TYPES_KEY = RegistryKey.ofRegistry(
			Identifier. of(ModMarioQuaMario.MOD_ID, "stomp_types"));
	public static final Registry<StompType> STOMP_TYPES = FabricRegistryBuilder.createSimple(STOMP_TYPES_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final GameRules.Key<GameRules.BooleanRule> USE_CHARACTER_STATS =
			GameRuleRegistry.register("useMarioCharacterStats", GameRules.Category.PLAYER,
					GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
						for(ServerPlayerEntity player : PlayerLookup.all(server)) {
							ServerPlayNetworking.send(player, new MarioPackets.SetUseCharacterStatsPayload(booleanRule.get()));
						}
					}));

	public static final Identifier JUMP_SOUND_ID = Identifier.of(ModMarioQuaMario.MOD_ID, "jump");
	public static final SoundEvent JUMP_SOUND_EVENT = SoundEvent.of(JUMP_SOUND_ID);

	public static void register() {
		registerCharacters();
		registerPowerUps();
		registerStompTypes();

		Registry.register(Registries.SOUND_EVENT, JUMP_SOUND_ID, JUMP_SOUND_EVENT);
	}

	public static void registerCharacters() {
		CharaMario.INSTANCE.register();
		Luigi.INSTANCE.register();
	}
	public static void registerPowerUps() {
		SmallForm.INSTANCE.register();
		SuperForm.INSTANCE.register();
		FireForm.INSTANCE.register();
	}
	public static void registerStompTypes() {
		StompBasic.INSTANCE.register();
	}
}
