package fqf.qua_mario;

import com.google.common.reflect.Reflection;
import com.sun.jna.internal.ReflectionUtils;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.characters.characters.Luigi;
import fqf.qua_mario.mariostates.MarioState;
import fqf.qua_mario.mariostates.states.airborne.Backflip;
import fqf.qua_mario.neostates.NeoMarioState;
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
import org.apache.logging.log4j.core.util.ReflectionUtil;
import sun.reflect.ReflectionFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	public static final RegistryKey<DamageType> STOMP_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "stomp"));
	public static final RegistryKey<DamageType> SPIN_JUMP_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "spin_jump"));
	public static final RegistryKey<DamageType> GROUND_POUND_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ModMarioQuaMario.MOD_ID, "ground_pound"));

	public static final Identifier JUMP_SOUND_ID = Identifier.of(ModMarioQuaMario.MOD_ID, "sfx.jump");
	public static final SoundEvent JUMP_SOUND_EVENT = SoundEvent.of(JUMP_SOUND_ID);

	public static final Identifier STOMP_SOUND_ID = Identifier.of(ModMarioQuaMario.MOD_ID, "sfx.stomp");
	public static final SoundEvent STOMP_SOUND_EVENT = SoundEvent.of(STOMP_SOUND_ID);

	public static void register() {

		registerPowerUps();
		registerCharacters(); // Characters have to be registered after power-ups so they know which power-ups they need models for!!
		registerStompTypes();
		registerStates();

		Registry.register(Registries.SOUND_EVENT, JUMP_SOUND_ID, JUMP_SOUND_EVENT);
		Registry.register(Registries.SOUND_EVENT, STOMP_SOUND_ID, STOMP_SOUND_EVENT);
	}

	public static void registerStates() {
		List<NeoMarioState> uwu = FabricLoader.getInstance().getEntrypointContainers("uwu", NeoMarioState.class).stream().map(EntrypointContainer::getEntrypoint).toList();
		for(NeoMarioState boogula : uwu) {
			ModMarioQuaMario.LOGGER.info("Boogula: " + boogula.getID());
		}
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
