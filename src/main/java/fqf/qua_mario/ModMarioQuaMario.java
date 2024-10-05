package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.SuperForm;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModMarioQuaMario implements ModInitializer {
	public static final String MOD_ID = "qua_mario";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ModConfig CONFIG;
	public static final Map<PlayerEntity, MarioPlayerInfo> PLAYER_DATA = new HashMap<>();

	static {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public static boolean playerIsMarioClient(PlayerEntity player) {
		return player.isMainPlayer();
//		return(
//				player.getWorld().isClient
//				&& player instanceof ClientPlayerEntity clientPlayer
//				&& clientPlayer.equals(MarioClient.player)
//		);
	}

	private static void sendMarioUpdatePacket(PlayerEntity changingPlayer, CustomPayload packet) {
		Collection<ServerPlayerEntity> sendToPlayers = PlayerLookup.tracking(changingPlayer);
		sendToPlayers.add((ServerPlayerEntity) changingPlayer);
		for(ServerPlayerEntity player : sendToPlayers) {
			ServerPlayNetworking.send(player, packet);
		}
	}

	public static boolean getIsMario(PlayerEntity player) {
		if(playerIsMarioClient(player))
			return MarioClient.isMario;
		else if(PLAYER_DATA.containsKey(player))
			return PLAYER_DATA.get(player).isMario;
		else {
			LOGGER.trace("Tried to check if someone is Mario but we don't know?!");
			return false;
		}
	}

	public static boolean getUseCharacterStats(PlayerEntity player) {
		if(player.getWorld().isClient) return MarioClient.useCharacterStats;
		return player.getWorld().getGameRules().getBoolean(MarioRegistries.USE_CHARACTER_STATS);
	}

	@NotNull public static MarioCharacter getCharacter(PlayerEntity player) {
		if(playerIsMarioClient(player))
			return MarioClient.character;
		else if(PLAYER_DATA.containsKey(player))
			return PLAYER_DATA.get(player).character;
		else {
			LOGGER.trace("Tried to get Character of someone else but we don't know?!");
			return CharaMario.INSTANCE;
		}
	}

	@NotNull public static PowerUp getPowerUp(PlayerEntity player) {
		if(playerIsMarioClient(player)) {
			return MarioClient.powerUp;
		}
		else if(PLAYER_DATA.containsKey(player))
			return PLAYER_DATA.get(player).powerUp;
		else {
			LOGGER.trace("Tried to get Power-up of someone else but we don't know?!");
			return SuperForm.INSTANCE;
		}
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

	public static MarioPlayerInfo ensureMarioData(PlayerEntity player) {
		PLAYER_DATA.putIfAbsent(player, new MarioPlayerInfo(true, CharaMario.INSTANCE, SuperForm.INSTANCE));
		return PLAYER_DATA.get(player);
	}

	public static String setIsMario(PlayerEntity player, boolean isMario) {
		if(!player.getWorld().isClient)
			sendMarioUpdatePacket(player, new MarioPackets.SetMarioEnabledPayload(player.getId(), isMario));
		else if(playerIsMarioClient(player))
			MarioClient.isMario = isMario;

		ensureMarioData(player).isMario = isMario;
		player.calculateDimensions();

		// Change the player's playermodel to the current character & power-up state
		String newModel = getCharacter(player).getModel(getPowerUp(player));
		LOGGER.info("setIsMario: Switched to playermodel {}", newModel);

		// Return string in case this is being used for a command
		return (isMario ? "Enabled" : "Disabled") + " Mario mode for " + player + ".";
	}

	public static String setCharacter(PlayerEntity player, MarioCharacter character) {
		if(!player.getWorld().isClient)
			sendMarioUpdatePacket(player, new MarioPackets.SetCharacterPayload(player.getId(), MarioRegistries.CHARACTERS.getRawId(character)));
		else if(playerIsMarioClient(player))
			MarioClient.character = character;

		ensureMarioData(player).character = character;
		player.calculateDimensions();

		// Change the player's playermodel to the current character & power-up state
		String newModel = character.getModel(getPowerUp(player));
		LOGGER.info("setCharacter: Switched to playermodel {}", newModel);

		// Return string in case this is being used for a command
		return player + " will now be " + character.getName();
	}

	public static String setPowerUp(PlayerEntity player, PowerUp powerUp) {
		if(!player.getWorld().isClient) {
			sendMarioUpdatePacket(player, new MarioPackets.SetPowerUpPayload(player.getId(), MarioRegistries.POWER_UPS.getRawId(powerUp)));
		}
		else if(playerIsMarioClient(player))
			MarioClient.powerUp = powerUp;

		ensureMarioData(player).powerUp = powerUp;
		player.calculateDimensions();

		// Change the player's playermodel to the current character & power-up state
		String newModel = getCharacter(player).getModel(powerUp);
		LOGGER.info("setPowerUp: Switched to playermodel {}", newModel);

		// Return string in case this is being used for a command
		return player + " will now be " + powerUp.getFormName(getCharacter(player));
	}

	public static String setFullMarioData(PlayerEntity player, boolean isMario, MarioCharacter character, PowerUp powerUp) {
		if(!player.getWorld().isClient) {
			if(((ServerPlayerEntity) player).networkHandler != null)
				sendMarioUpdatePacket(player, new MarioPackets.FullSyncPayload(
						player.getId(),
						isMario,
						MarioRegistries.CHARACTERS.getRawId(character),
						MarioRegistries.POWER_UPS.getRawId(powerUp)
				));
		}
		else {
			if(playerIsMarioClient(player)) {
				MarioClient.isMario = isMario;
				MarioClient.character = character;
				MarioClient.powerUp = powerUp;
			}
		}

		MarioPlayerInfo marioData = ensureMarioData(player);
		marioData.isMario = isMario;
		marioData.character = character;
		marioData.powerUp = powerUp;

		return player + " is now set to " + powerUp.getFormName(character) + (isMario ? "." : ". (Disabled)");
	}

	@Override
	public void onInitialize() {
		MarioPackets.registerCommon();
		MarioPackets.registerServer();
		MarioRegistries.register();
		VoiceLine.register();
		MarioCommand.registerMarioCommand();
	}

	public static class MarioPlayerInfo {
		public boolean isMario;
		public MarioCharacter character;
		public PowerUp powerUp;
		private MarioPlayerInfo(boolean isMario, MarioCharacter character, PowerUp powerUp) {
			this.isMario = isMario;
			this.character = character;
			this.powerUp = powerUp;
		}
	}
}