package fqf.qua_mario;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

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
		ClientPlayNetworking.send(new MarioPackets.BroadcastJumpSfxPayload(false));
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
		ClientPlayNetworking.send(new MarioPackets.BroadcastJumpSfxPayload(true));
	}

	public static void fadeJumpSound(PlayerEntity jumper) {
		JUMP_IS_FADING.put(jumper, true);
	}

//	PositionedSoundInstance
}
