package fqf.qua_mario;

import fqf.qua_mario.stomptypes.StompHandler;
import fqf.qua_mario.util.MarioDataSaver;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MarioPackets {
	public static void registerCommon() {
		PayloadTypeRegistry.playS2C().register(MarioPackets.InitialSyncPayload.ID, MarioPackets.InitialSyncPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(MarioPackets.SetMarioEnabledPayload.ID, MarioPackets.SetMarioEnabledPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(StompHandler.affirmStompPayload.ID, StompHandler.affirmStompPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.SetUseCharacterStatsPayload.ID, MarioPackets.SetUseCharacterStatsPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.SetCharacterPayload.ID, MarioPackets.SetCharacterPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.SetPowerUpPayload.ID, MarioPackets.SetPowerUpPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.PlayJumpSfxPayload.ID, MarioPackets.PlayJumpSfxPayload.CODEC);

		PayloadTypeRegistry.playC2S().register(BroadcastJumpSfxPayload.ID, BroadcastJumpSfxPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(StompHandler.requestStompPayload.ID, StompHandler.requestStompPayload.CODEC);
	}

	public static void registerServer() {
		// this also runs on the client but who cares. shut up. it's the stuff that matters to the server
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			MarioDataSaver playerDataSaver = (MarioDataSaver) (handler.player);
			ServerPlayNetworking.send(handler.player, new InitialSyncPayload(
					playerDataSaver.marioQuaMario$getPersistentData().getBoolean("isMario"),
					MarioRegistries.CHARACTERS.getRawId(playerDataSaver.marioQuaMario$getCharacter()),
					MarioRegistries.POWER_UPS.getRawId(playerDataSaver.marioQuaMario$getPowerUp()),
					handler.player.getWorld().getGameRules().getBoolean(MarioRegistries.USE_CHARACTER_STATS)
			));
		});

		ServerPlayNetworking.registerGlobalReceiver(BroadcastJumpSfxPayload.ID, (payload, context) -> {
			ModMarioQuaMario.LOGGER.info("Received the packet asking to play a sound effect");
			for(ServerPlayerEntity player : PlayerLookup.tracking(context.player())) {
				if(player != context.player())
					ServerPlayNetworking.send(player, new PlayJumpSfxPayload(context.player().getId(), payload.isFading));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(StompHandler.requestStompPayload.ID, StompHandler::parseRequestStompPacket);
	}

	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.InitialSyncPayload.ID, (payload, context) -> {
			MarioClient.isMario = payload.isMario();
			MarioClient.character = MarioRegistries.CHARACTERS.get(payload.newCharacter());
			MarioClient.powerUp = MarioRegistries.POWER_UPS.get(payload.newPowerUp());
			MarioClient.useCharacterStats = payload.useCharacterStats();
		});

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetMarioEnabledPayload.ID, (payload, context) ->
				MarioClient.isMario = payload.isMario());

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetCharacterPayload.ID, (payload, context) ->
				MarioClient.character = MarioRegistries.CHARACTERS.get(payload.newCharacter()));

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetPowerUpPayload.ID, (payload, context) ->
				MarioClient.powerUp = MarioRegistries.POWER_UPS.get(payload.newPowerUp()));

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetUseCharacterStatsPayload.ID, (payload, context) ->
				MarioClient.useCharacterStats = payload.useCharacterStats());

		ClientPlayNetworking.registerGlobalReceiver(StompHandler.affirmStompPayload.ID, StompHandler::parseAffirmStompPacket);

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.PlayJumpSfxPayload.ID, (payload, context) -> {
			if(payload.isFading)
				SoundFader.fadeJumpSound((PlayerEntity) context.player().getWorld().getEntityById(payload.player));
			else
				SoundFader.playJumpSound((PlayerEntity) context.player().getWorld().getEntityById(payload.player));
		});
	}

	public record InitialSyncPayload(boolean isMario, int newCharacter, int newPowerUp, boolean useCharacterStats) implements CustomPayload {
		public static final Id<InitialSyncPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "initial_sync"));
		public static final PacketCodec<RegistryByteBuf, InitialSyncPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.BOOL, InitialSyncPayload::isMario,
				PacketCodecs.INTEGER, InitialSyncPayload::newCharacter,
				PacketCodecs.INTEGER, InitialSyncPayload::newPowerUp,
				PacketCodecs.BOOL, InitialSyncPayload::useCharacterStats,
				InitialSyncPayload::new
		);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetMarioEnabledPayload(boolean isMario) implements CustomPayload {
		public static final Id<SetMarioEnabledPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "set_mario_enabled"));
		public static final PacketCodec<RegistryByteBuf, SetMarioEnabledPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SetMarioEnabledPayload::isMario, SetMarioEnabledPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetUseCharacterStatsPayload(boolean useCharacterStats) implements CustomPayload {
		public static final Id<SetUseCharacterStatsPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "set_use_character_stats"));
		public static final PacketCodec<RegistryByteBuf, SetUseCharacterStatsPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SetUseCharacterStatsPayload::useCharacterStats, SetUseCharacterStatsPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetCharacterPayload(int newCharacter) implements CustomPayload {
		public static final Id<SetCharacterPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "change_character"));
		public static final PacketCodec<RegistryByteBuf, SetCharacterPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SetCharacterPayload::newCharacter, SetCharacterPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetPowerUpPayload(int newPowerUp) implements CustomPayload {
		public static final Id<SetPowerUpPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "change_power_up"));
		public static final PacketCodec<RegistryByteBuf, SetPowerUpPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SetPowerUpPayload::newPowerUp, SetPowerUpPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record BroadcastJumpSfxPayload(boolean isFading) implements CustomPayload {
		public static final Id<BroadcastJumpSfxPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "broadcast_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, BroadcastJumpSfxPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, BroadcastJumpSfxPayload::isFading, BroadcastJumpSfxPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record PlayJumpSfxPayload(int player, boolean isFading) implements CustomPayload {
		public static final Id<PlayJumpSfxPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "play_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, PlayJumpSfxPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, PlayJumpSfxPayload::player,
				PacketCodecs.BOOL, PlayJumpSfxPayload::isFading,
				PlayJumpSfxPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}
}
