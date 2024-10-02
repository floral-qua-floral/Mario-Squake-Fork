package fqf.qua_mario;

import fqf.qua_mario.characters.CharaStat;
import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.FireForm;
import fqf.qua_mario.powerups.forms.SmallForm;
import fqf.qua_mario.powerups.forms.SuperForm;
import fqf.qua_mario.util.MarioDataSaver;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.Identifier;

public class ModMarioQuaMario implements ModInitializer {
	public static final String MOD_ID = "qua_mario";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);





//	Registry.register()

	public static final ModConfig CONFIG;
	static {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public static boolean useMarioPhysics(PlayerEntity player, boolean mustBeClient) {
		return(
				// Has to be client-side if required by parameter
				(player.getWorld().isClient || !mustBeClient)
				// Has to be Mario (duh)
				&& (player.getWorld().isClient ? MarioClient.isMario : ((MarioDataSaver) player).marioQuaMario$getPersistentData().getBoolean("isMario"))
				// Can't be creative-flying or gliding with an Elytra
				&& !player.getAbilities().flying && !player.isFallFlying()
				// Can't be in a vehicle
				&& !player.hasVehicle()
				// Can't be climbing
				&& !player.isClimbing()
		);
	}

//	FabricRegistryBuilder

	public static void sendSetMarioPacket(ServerPlayerEntity player, boolean isMario) {
		ServerPlayNetworking.send(player, new SetMarioEnabledPayload(isMario));
	}

	public static NbtCompound getMarioPersistentData(ServerPlayerEntity player) {
		return ((MarioDataSaver) player).marioQuaMario$getPersistentData();
	}

	public static double getMarioStat(PlayerEntity player, CharaStat stat) {
		if(player.getWorld().isClient) {
			return MarioClient.getStat(stat);
		}
		else {

			return stat.getDefaultValue();
		}
	}

	public static String setCharacter(ServerPlayerEntity player, MarioCharacter character) {
		((MarioDataSaver) player).marioQuaMario$setCharacter(character);
		// Change the player's playermodel to the current character & power-up state
		// Send a "change character" packet
		return player + " will now be " + character.getName();
	}

	public static String setPowerUp(ServerPlayerEntity player, PowerUp powerUp) {
		((MarioDataSaver) player).marioQuaMario$setPowerUp(powerUp);
		// Change the player's playermodel to the current power-up state
		// Send a "change power-up" packet
		return player + " will now be " + powerUp.getFormName(((MarioDataSaver) player).marioQuaMario$getCharacter());
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(SetMarioEnabledPayload.ID, SetMarioEnabledPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(StompAttack.affirmStompPayload.ID, StompAttack.affirmStompPayload.CODEC);

		PayloadTypeRegistry.playC2S().register(PlayJumpSfxPayload.ID, PlayJumpSfxPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(StompAttack.requestStompPayload.ID, StompAttack.requestStompPayload.CODEC);

//		Registry.register(POWER_UPS, SmallForm.INSTANCE.getID(), SmallForm.INSTANCE);
//		Registry.register(POWER_UPS, SuperForm.INSTANCE.getID(), SuperForm.INSTANCE);
//		Registry.register(POWER_UPS, FireForm.INSTANCE.getID(), FireForm.INSTANCE);

		MarioRegistries.register();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			MarioDataSaver playerDataSaver = (MarioDataSaver) (handler.player);
			sendSetMarioPacket(handler.player, playerDataSaver.marioQuaMario$getPersistentData().getBoolean("isMario"));
		});

		ServerPlayNetworking.registerGlobalReceiver(PlayJumpSfxPayload.ID, (payload, context) -> {
			LOGGER.info("Received the packet asking to play a sound effect");
		});

		ServerPlayNetworking.registerGlobalReceiver(StompAttack.requestStompPayload.ID, (payload, context) -> {
			LOGGER.info("Received the packet asking to stomp something");
			StompAttack.server_receive(payload, context);
		});

		MarioCommand.registerMarioCommand();
	}

	public record SetMarioEnabledPayload(boolean isMario) implements CustomPayload {
		public static final CustomPayload.Id<SetMarioEnabledPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "set_mario_enabled"));
		public static final PacketCodec<RegistryByteBuf, SetMarioEnabledPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SetMarioEnabledPayload::isMario, SetMarioEnabledPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record PlayJumpSfxPayload(boolean isSpin) implements CustomPayload {
		public static final CustomPayload.Id<PlayJumpSfxPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "play_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, PlayJumpSfxPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, PlayJumpSfxPayload::isSpin, PlayJumpSfxPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}
}