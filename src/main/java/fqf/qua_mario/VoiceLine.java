package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum VoiceLine {
	SELECT,
	DUCK,

	DOUBLE_JUMP,
	TRIPLE_JUMP,
	TRIPLE_JUMP_SALUTE,

	DUCK_JUMP,
	LONG_JUMP,
	BACKFLIP,
	SIDEFLIP,
	WALL_JUMP,

	REVERT,
	BURNT,

	FIREBALL,
	GET_STAR
	;
//	private final Identifier SOUND_IDENTIFIER;
//	private final SoundEvent SOUND_EVENT;

	private static final VoiceLine[] VOICE_LINE_VALUES = VoiceLine.values();
	private static final Map<PlayerEntity, PositionedSoundInstance> PLAYER_VOICE_LINES = new HashMap<>();
	private final Map<MarioCharacter, SoundEvent> SOUND_EVENTS;

	VoiceLine() {
		SOUND_EVENTS = new HashMap<>();

		for(MarioCharacter character : MarioRegistries.CHARACTERS) {
			Identifier id = Identifier.of(ModMarioQuaMario.MOD_ID, "voice." + character.getID().getPath() + "." + this.name().toLowerCase());
			SoundEvent event = SoundEvent.of(id);
			SOUND_EVENTS.put(character, event);
			Registry.register(Registries.SOUND_EVENT, id, event);
		}
	}
	public SoundEvent getEvent(MarioCharacter character) {
		return SOUND_EVENTS.get(character);
	}

	public void broadcast() {
		long seed = RandomSeed.getSeed();
		play(MarioClient.player, seed);
		ModMarioQuaMario.LOGGER.info("broadcast() Seed: " + seed);
		ClientPlayNetworking.send(new BroadcastVoiceLinePayload(MarioClient.player.getId(), this.ordinal(), seed));
	}
	public void play(PlayerEntity player, long seed) {
		PositionedSoundInstance previousVoiceLine = PLAYER_VOICE_LINES.get(player);
		SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
		soundManager.stop(previousVoiceLine);

		PositionedSoundInstance newSound = new PositionedSoundInstance(
				getEvent(ModMarioQuaMario.getCharacter(player)),
				SoundCategory.VOICE,
				1.0F,
				1.0F,
				Random.create(seed),
				player.getX(),
				player.getY(),
				player.getZ()
		);
		soundManager.play(newSound);
		PLAYER_VOICE_LINES.put(player, newSound);
	}

	public static void parseBroadcastVoiceLinePayload(BroadcastVoiceLinePayload payload, ServerPlayNetworking.Context context) {
		ModMarioQuaMario.LOGGER.info("parseBroadcastVoiceLinePayload() Seed: " + payload.randomSeed);
		Collection<ServerPlayerEntity> sendToPlayers = PlayerLookup.tracking(context.player());
		for(ServerPlayerEntity player : sendToPlayers) {
			if(player.equals(context.player())) continue;
			ServerPlayNetworking.send(player, new PlayVoiceLinePayload(context.player().getId(), payload.voiceLineOrdinal, payload.randomSeed));
		}
	}

	public static void parsePlayVoiceLinePayload(PlayVoiceLinePayload payload, ClientPlayNetworking.Context context) {
		ModMarioQuaMario.LOGGER.info("parsePlayVoiceLinePayload() Seed: " + payload.randomSeed);
		VoiceLine playLine = VOICE_LINE_VALUES[payload.voiceLineOrdinal];
		playLine.play((PlayerEntity) context.player().getWorld().getEntityById(payload.player), payload.randomSeed);
	}

	public record BroadcastVoiceLinePayload(int player, int voiceLineOrdinal, long randomSeed) implements CustomPayload {
		public static final Id<BroadcastVoiceLinePayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "broadcast_voice_line"));
		public static final PacketCodec<RegistryByteBuf, BroadcastVoiceLinePayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, BroadcastVoiceLinePayload::player,
				PacketCodecs.INTEGER, BroadcastVoiceLinePayload::voiceLineOrdinal,
				PacketCodecs.VAR_LONG, BroadcastVoiceLinePayload::randomSeed,
				BroadcastVoiceLinePayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public record PlayVoiceLinePayload(int player, int voiceLineOrdinal, long randomSeed) implements CustomPayload {
		public static final Id<PlayVoiceLinePayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "play_voice_line"));
		public static final PacketCodec<RegistryByteBuf, PlayVoiceLinePayload> CODEC = PacketCodec.tuple(
				PacketCodecs.INTEGER, PlayVoiceLinePayload::player,
				PacketCodecs.INTEGER, PlayVoiceLinePayload::voiceLineOrdinal,
				PacketCodecs.VAR_LONG, PlayVoiceLinePayload::randomSeed,
				PlayVoiceLinePayload::new);

		@Override
		public Id<? extends CustomPayload> getId() { return ID; }
	}

	public static void register() {}
}
