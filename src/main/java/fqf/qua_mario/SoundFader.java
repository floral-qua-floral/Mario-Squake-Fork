package fqf.qua_mario;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SoundFader {
	public static final Map<PlayerEntity, Boolean> JUMP_IS_FADING = new HashMap<>();

	public static class JumpSoundInstance extends PositionedSoundInstance implements TickableSoundInstance {
		private final PlayerEntity owner;

		public JumpSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, double x, double y, double z, PlayerEntity player) {
			super(sound, category, volume, pitch, random, x, y, z);
			owner = player;
		}

		@Override
		public boolean isDone() {
			return volume <= 0.0F;
		}

		@Override
		public void tick() {
			if(JUMP_IS_FADING.get(owner))
				this.volume -= 0.2F;
		}
	}

	public static void broadcastAndPlayJumpSound() {
		playJumpSound(MarioClient.player);
		ClientPlayNetworking.send(new BroadcastJumpSfxPayload(false));
	}

	public static void playJumpSound(PlayerEntity jumper) {
		JUMP_IS_FADING.put(jumper, false);
		JumpSoundInstance newSound = new JumpSoundInstance(
				MarioRegistries.JUMP_SOUND_EVENT,
				SoundCategory.PLAYERS,
				1.0F,
				1.0F,
				SoundInstance.createRandom(),
				jumper.getX(),
				jumper.getY(),
				jumper.getZ(),
				jumper
		);
		SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();

		soundManager.play(newSound);

//		AmbientSoundLoops
	}

	public static void broadcastAndFadeJumpSound() {
		fadeJumpSound(MarioClient.player);
		ClientPlayNetworking.send(new BroadcastJumpSfxPayload(true));
	}

	public static void fadeJumpSound(PlayerEntity jumper) {
		JUMP_IS_FADING.put(jumper, true);
	}

	public static void parseBroadcastJumpSfxPayload(BroadcastJumpSfxPayload payload, ServerPlayNetworking.Context context) {
		ModMarioQuaMario.LOGGER.info("Received the packet asking to play a sound effect");
		Collection<ServerPlayerEntity> sendToPlayers = PlayerLookup.tracking(context.player());
		for(ServerPlayerEntity player : sendToPlayers) {
			if(player.equals(context.player())) continue;
			ServerPlayNetworking.send(player, new SoundFader.PlayJumpSfxPayload(context.player().getId(), payload.isFading));
		}
	}

	public static void parsePlayJumpSfxPayload(PlayJumpSfxPayload payload, ClientPlayNetworking.Context context) {
		if(payload.isFading)
			fadeJumpSound((PlayerEntity) context.player().getWorld().getEntityById(payload.player));
		else
			playJumpSound((PlayerEntity) context.player().getWorld().getEntityById(payload.player));
	}

	public record BroadcastJumpSfxPayload(boolean isFading) implements CustomPayload {
		public static final Id<BroadcastJumpSfxPayload> ID = new Id<>(Identifier.of(ModMarioQuaMario.MOD_ID, "broadcast_jump_sfx"));
		public static final PacketCodec<RegistryByteBuf, BroadcastJumpSfxPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.BOOL, BroadcastJumpSfxPayload::isFading,
				BroadcastJumpSfxPayload::new);

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

//	PositionedSoundInstance
}
