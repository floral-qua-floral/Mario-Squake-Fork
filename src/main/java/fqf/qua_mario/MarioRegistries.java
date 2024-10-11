package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.characters.characters.Luigi;
import fqf.qua_mario.neostates.StateDefinition;
import fqf.qua_mario.neostates.ParsedState;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.FireForm;
import fqf.qua_mario.powerups.forms.MiniForm;
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
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarioRegistries {
	public static final RegistryKey<Registry<ParsedState>> STATES_KEY = RegistryKey.ofRegistry(
			Identifier.of(ModMarioQuaMario.MOD_ID, "states"));
	public static final Registry<ParsedState> STATES = FabricRegistryBuilder.createSimple(STATES_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final RegistryKey<Registry<MarioCharacter>> CHARACTERS_KEY = RegistryKey.ofRegistry(
			Identifier.of(ModMarioQuaMario.MOD_ID, "characters"));
	public static final Registry<MarioCharacter> CHARACTERS = FabricRegistryBuilder.createSimple(CHARACTERS_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final RegistryKey<Registry<PowerUp>> POWER_UPS_KEY = RegistryKey.ofRegistry(
			Identifier.of(ModMarioQuaMario.MOD_ID, "power_ups"));
	public static final Registry<PowerUp> POWER_UPS = FabricRegistryBuilder.createSimple(POWER_UPS_KEY)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

	public static final RegistryKey<Registry<StompType>> STOMP_TYPES_KEY = RegistryKey.ofRegistry(
			Identifier.of(ModMarioQuaMario.MOD_ID, "stomp_types"));
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

	public static final RegistryKey<DamageType> STOMP_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "stomp"));
	public static final RegistryKey<DamageType> SPIN_JUMP_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "spin_jump"));
	public static final RegistryKey<DamageType> GROUND_POUND_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "ground_pound"));

	public static final Identifier JUMP_SOUND_ID = Identifier.of(ModMarioQuaMario.MOD_ID, "sfx.jump");
	public static final SoundEvent JUMP_SOUND_EVENT = SoundEvent.of(JUMP_SOUND_ID);

	public static final Identifier STOMP_SOUND_ID = Identifier.of(ModMarioQuaMario.MOD_ID, "sfx.stomp");
	public static final SoundEvent STOMP_SOUND_EVENT = SoundEvent.of(STOMP_SOUND_ID);

	public static void register() {
		registerStates();
		registerPowerUps();
		registerCharacters(); // Characters have to be registered after power-ups so they know which power-ups they need models for!!
		registerStompTypes();


		Registry.register(Registries.SOUND_EVENT, JUMP_SOUND_ID, JUMP_SOUND_EVENT);
		Registry.register(Registries.SOUND_EVENT, STOMP_SOUND_ID, STOMP_SOUND_EVENT);
	}

	public static void registerStates() {
		ModMarioQuaMario.LOGGER.info("Registering action states...");
		List<StateDefinition> allStateDefinitions = FabricLoader.getInstance().getEntrypointContainers("mariostates", StateDefinition.class).stream().map(EntrypointContainer::getEntrypoint).toList();
		for(StateDefinition stateDefinition : allStateDefinitions) {
			ModMarioQuaMario.LOGGER.info("Entrypoint found state definition for: {}", stateDefinition.getID());

			ParsedState state = new ParsedState(stateDefinition);
			Registry.register(STATES, state.IDENTIFIER, state);
		}

		for(ParsedState finishMe : STATES) {
			ModMarioQuaMario.LOGGER.info("Populating transition lists for {}", finishMe.IDENTIFIER);
			finishMe.populateTransitionLists();
		}

		STATES.freeze();

		MarioClient.neostate = STATES.get(Identifier.of("qua_mario:standing_test"));
	}

	public static void registerPowerUps() {
		ModMarioQuaMario.LOGGER.info("Registering power-up states...");
		SmallForm.INSTANCE.register();
		SuperForm.INSTANCE.register();
		FireForm.INSTANCE.register();
		MiniForm.INSTANCE.register();
	}
	public static void registerCharacters() {
		ModMarioQuaMario.LOGGER.info("Registering characters...");
		CharaMario.INSTANCE.register();
		Luigi.INSTANCE.register();
	}
	public static void registerStompTypes() {
		ModMarioQuaMario.LOGGER.info("Registering stomp types...");
		StompBasic.INSTANCE.register();
	}
}
