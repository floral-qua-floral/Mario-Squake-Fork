package fqf.qua_mario;

import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.SuperForm;
import fqf.qua_mario.util.MarioDataSaver;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModMarioQuaMario implements ModInitializer {
	public static final String MOD_ID = "qua_mario";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ModConfig CONFIG;
	static {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public static boolean getIsMario(PlayerEntity player) {
		if(player.getWorld().isClient) {
			if(player == MarioClient.player)
				return MarioClient.isMario;
			else if(ModMarioQuaMarioClient.OTHER_PLAYER_DATA.containsKey(player))
				return ModMarioQuaMarioClient.OTHER_PLAYER_DATA.get(player).isMario;
			else {
				LOGGER.error("Tried to check if someone is Mario but we don't know?!");
				return false;
			}
		}
		return ((MarioDataSaver) player).marioQuaMario$getPersistentData().getBoolean("isMario");
	}

	@NotNull
	public static MarioCharacter getCharacter(PlayerEntity player) {
		if(player.getWorld().isClient) {
			if(player == MarioClient.player)
				return MarioClient.getCharacter();
			else if(ModMarioQuaMarioClient.OTHER_PLAYER_DATA.containsKey(player))
				return ModMarioQuaMarioClient.OTHER_PLAYER_DATA.get(player).character;
			else {
				LOGGER.error("Tried to get Character of someone else but we don't know?!");
				return CharaMario.INSTANCE;
			}
		}
		return ((MarioDataSaver) player).marioQuaMario$getCharacter();
	}
	public static PowerUp getPowerUp(PlayerEntity player) {
		if(player.getWorld().isClient) {
			if(player == MarioClient.player)
				return MarioClient.getPowerUp();
			else if(ModMarioQuaMarioClient.OTHER_PLAYER_DATA.containsKey(player))
				return ModMarioQuaMarioClient.OTHER_PLAYER_DATA.get(player).powerUp;
			else {
				LOGGER.error("Tried to get Power-up of someone else but we don't know?!");
				return SuperForm.INSTANCE;
			}
		}
		return ((MarioDataSaver) player).marioQuaMario$getPowerUp();
	}

	public static boolean useMarioPhysics(PlayerEntity player, boolean mustBeClient) {
		return(
				// Has to be client-side if required by parameter
				(player.getWorld().isClient || !mustBeClient)
				// Has to be Mario (duh)
				&& getIsMario(player)
				// Can't be creative-flying or gliding with an Elytra
				&& !player.getAbilities().flying && !player.isFallFlying()
				// Can't be in a vehicle
				&& !player.hasVehicle()
				// Can't be climbing
				&& !player.isClimbing()
		);
	}

	public static NbtCompound getMarioPersistentData(ServerPlayerEntity player) {
		return ((MarioDataSaver) player).marioQuaMario$getPersistentData();
	}

	public static double getMarioStat(PlayerEntity player, CharaStat stat) {
		if(player.getWorld().isClient) {
			return stat.getValue();
		}
		else {
			if(player.getWorld().getGameRules().getBoolean(MarioRegistries.USE_CHARACTER_STATS))
				return getCharacter(player).getStatValue(stat);
			return stat.getDefaultValue();
		}
	}

	public static String setIsMario(ServerPlayerEntity player, boolean isMario) {
		ModMarioQuaMario.getMarioPersistentData(player).putBoolean("isMario", isMario);
		ServerPlayNetworking.send(player, new MarioPackets.SetMarioEnabledPayload(isMario));

		player.calculateDimensions();
		return (isMario ? "Enabled" : "Disabled") + " Mario mode for " + player + ".";
	}

	public static String setCharacter(ServerPlayerEntity player, MarioCharacter character) {
		((MarioDataSaver) player).marioQuaMario$setCharacter(character);
		// Change the player's playermodel to the current character & power-up state
		ServerPlayNetworking.send(player, new MarioPackets.SetCharacterPayload(MarioRegistries.CHARACTERS.getRawId(character)));
		player.calculateDimensions();
		return player + " will now be " + character.getName();
	}

	public static String setPowerUp(ServerPlayerEntity player, PowerUp powerUp) {
		((MarioDataSaver) player).marioQuaMario$setPowerUp(powerUp);
		// Change the player's playermodel to the current power-up state
		ServerPlayNetworking.send(player, new MarioPackets.SetPowerUpPayload(MarioRegistries.POWER_UPS.getRawId(powerUp)));
		player.calculateDimensions();
		return player + " will now be " + powerUp.getFormName(((MarioDataSaver) player).marioQuaMario$getCharacter());
	}

	@Override
	public void onInitialize() {
		MarioPackets.registerCommon();
		MarioPackets.registerServer();



		MarioRegistries.register();



		MarioCommand.registerMarioCommand();
	}

}