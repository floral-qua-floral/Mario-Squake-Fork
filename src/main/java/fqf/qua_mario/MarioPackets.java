package fqf.qua_mario;

import fqf.qua_mario.stomptypes.StompHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class MarioPackets {
	public static void registerCommon() {
		PayloadTypeRegistry.playS2C().register(FullSyncPayload.ID, FullSyncPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(MarioPackets.SetMarioEnabledPayload.ID, MarioPackets.SetMarioEnabledPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(StompHandler.affirmStompPayload.ID, StompHandler.affirmStompPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.SetUseCharacterStatsPayload.ID, MarioPackets.SetUseCharacterStatsPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.SetCharacterPayload.ID, MarioPackets.SetCharacterPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(MarioPackets.SetPowerUpPayload.ID, MarioPackets.SetPowerUpPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SoundFader.PlayJumpSfxPayload.ID, SoundFader.PlayJumpSfxPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(VoiceLine.PlayVoiceLinePayload.ID, VoiceLine.PlayVoiceLinePayload.CODEC);

		PayloadTypeRegistry.playC2S().register(StompHandler.requestStompPayload.ID, StompHandler.requestStompPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SoundFader.BroadcastJumpSfxPayload.ID, SoundFader.BroadcastJumpSfxPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(VoiceLine.BroadcastVoiceLinePayload.ID, VoiceLine.BroadcastVoiceLinePayload.CODEC);
	}

	public static void registerServer() {
		// this also runs on the client but who cares. shut up. it's the stuff that matters to the server
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ModMarioQuaMario.LOGGER.info("JOIN event triggered!");
			ModMarioQuaMario.MarioPlayerInfo marioInfo = ModMarioQuaMario.ensureMarioData(handler.player);
			ServerPlayNetworking.send(handler.player, new FullSyncPayload(
					handler.player.getId(),
					marioInfo.isMario,
					MarioRegistries.CHARACTERS.getRawId(marioInfo.character),
					MarioRegistries.POWER_UPS.getRawId(marioInfo.powerUp)
			));
			ServerPlayNetworking.send(handler.player,
					new SetUseCharacterStatsPayload(handler.player.getWorld().getGameRules().getBoolean(MarioRegistries.USE_CHARACTER_STATS)));
		});

		EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
			if(entity instanceof PlayerEntity trackedPlayer) {
				ModMarioQuaMario.MarioPlayerInfo marioInfo = ModMarioQuaMario.ensureMarioData(trackedPlayer);
				ServerPlayNetworking.send(player, new FullSyncPayload(
						trackedPlayer.getId(),
						marioInfo.isMario,
						MarioRegistries.CHARACTERS.getRawId(marioInfo.character),
						MarioRegistries.POWER_UPS.getRawId(marioInfo.powerUp)
				));
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(SoundFader.BroadcastJumpSfxPayload.ID, SoundFader::parseBroadcastJumpSfxPayload);
		ServerPlayNetworking.registerGlobalReceiver(VoiceLine.BroadcastVoiceLinePayload.ID, VoiceLine::parseBroadcastVoiceLinePayload);
		ServerPlayNetworking.registerGlobalReceiver(StompHandler.requestStompPayload.ID, StompHandler::parseRequestStompPacket);
	}

	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(FullSyncPayload.ID, (payload, context) -> {
			ModMarioQuaMario.LOGGER.info("Full Sync Payload Received!");

			ModMarioQuaMario.setFullMarioData(
					getPlayerFromInt(context, payload.player),
					payload.isMario,
					MarioRegistries.CHARACTERS.get(payload.newCharacter),
					MarioRegistries.POWER_UPS.get(payload.newPowerUp())
			);
		});

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetMarioEnabledPayload.ID, (payload, context) ->
				ModMarioQuaMario.setIsMario(getPlayerFromInt(context, payload.player), payload.isMario));
//				MarioClient.isMario = payload.isMario());

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetCharacterPayload.ID, (payload, context) ->
				ModMarioQuaMario.setCharacter(getPlayerFromInt(context, payload.player),
						MarioRegistries.CHARACTERS.get(payload.newCharacter))
		);

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetPowerUpPayload.ID, (payload, context) ->
				ModMarioQuaMario.setPowerUp(getPlayerFromInt(context, payload.player),
						MarioRegistries.POWER_UPS.get(payload.newPowerUp()))
		);
//				MarioClient.setPowerUp(MarioRegistries.POWER_UPS.get(payload.newPowerUp())));

		ClientPlayNetworking.registerGlobalReceiver(MarioPackets.SetUseCharacterStatsPayload.ID, (payload, context) ->
				MarioClient.useCharacterStats = payload.useCharacterStats()
		);

		ClientPlayNetworking.registerGlobalReceiver(StompHandler.affirmStompPayload.ID, StompHandler::parseAffirmStompPacket);

		ClientPlayNetworking.registerGlobalReceiver(SoundFader.PlayJumpSfxPayload.ID, SoundFader::parsePlayJumpSfxPayload);
		ClientPlayNetworking.registerGlobalReceiver(VoiceLine.PlayVoiceLinePayload.ID, VoiceLine::parsePlayVoiceLinePayload);
	}

	public static PlayerEntity getPlayerFromInt(ClientPlayNetworking.Context context, int playerID) {
		return (PlayerEntity) context.player().getWorld().getEntityById(playerID);
	}

	public record FullSyncPayload(int player, boolean isMario, int newCharacter, int newPowerUp) implements CustomPayload {
		public static final Id<FullSyncPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "full_sync"));
		public static final PacketCodec<RegistryByteBuf, FullSyncPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, FullSyncPayload::player,
				PacketCodecs.BOOL, FullSyncPayload::isMario,
				PacketCodecs.INTEGER, FullSyncPayload::newCharacter,
				PacketCodecs.INTEGER, FullSyncPayload::newPowerUp,
				FullSyncPayload::new
		);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetMarioEnabledPayload(int player, boolean isMario) implements CustomPayload {
		public static final Id<SetMarioEnabledPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "set_mario_enabled"));
		public static final PacketCodec<RegistryByteBuf, SetMarioEnabledPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, SetMarioEnabledPayload::player,
				PacketCodecs.BOOL, SetMarioEnabledPayload::isMario,
				SetMarioEnabledPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetCharacterPayload(int player, int newCharacter) implements CustomPayload {
		public static final Id<SetCharacterPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "change_character"));
		public static final PacketCodec<RegistryByteBuf, SetCharacterPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, SetCharacterPayload::player,
				PacketCodecs.INTEGER, SetCharacterPayload::newCharacter,
				SetCharacterPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetPowerUpPayload(int player, int newPowerUp) implements CustomPayload {
		public static final Id<SetPowerUpPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "change_power_up"));
		public static final PacketCodec<RegistryByteBuf, SetPowerUpPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, SetPowerUpPayload::player,
				PacketCodecs.INTEGER, SetPowerUpPayload::newPowerUp,
				SetPowerUpPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record SetUseCharacterStatsPayload(boolean useCharacterStats) implements CustomPayload {
		public static final Id<SetUseCharacterStatsPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "set_use_character_stats"));
		public static final PacketCodec<RegistryByteBuf, SetUseCharacterStatsPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SetUseCharacterStatsPayload::useCharacterStats, SetUseCharacterStatsPayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}
}
