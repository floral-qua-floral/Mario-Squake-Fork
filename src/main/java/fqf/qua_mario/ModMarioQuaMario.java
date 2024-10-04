package fqf.qua_mario;

import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.util.MarioDataSaver;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
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
		return(player.getWorld().isClient ? MarioClient.isMario : ((MarioDataSaver) player).marioQuaMario$getPersistentData().getBoolean("isMario"));
	}
	public static MarioCharacter getCharacter(PlayerEntity player) {
		return(player.getWorld().isClient ? MarioClient.getCharacter() : ((MarioDataSaver) player).marioQuaMario$getCharacter());
	}
	public static PowerUp getPowerUp(PlayerEntity player) {
		return(player.getWorld().isClient ? MarioClient.getPowerUp() : ((MarioDataSaver) player).marioQuaMario$getPowerUp());
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