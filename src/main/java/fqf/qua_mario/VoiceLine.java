package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;

import java.util.HashMap;
import java.util.Map;

public enum VoiceLine {
	DOUBLE_JUMP("double_jump"),
	SIDEFLIP("sideflip");

//	private final Identifier SOUND_IDENTIFIER;
//	private final SoundEvent SOUND_EVENT;

	private static final Map<PlayerEntity, PositionedSoundInstance> PLAYER_VOICE_LINES = new HashMap<>();
	private final Map<MarioCharacter, Pair<Identifier, SoundEvent>> SOUND_EVENTS;

	VoiceLine(String eventName) {
		SOUND_EVENTS = new HashMap<>();

		for(MarioCharacter character : MarioRegistries.CHARACTERS) {
			Identifier id = Identifier.of(ModMarioQuaMario.MOD_ID, "voices/" + character.getID().getPath() + "/" + eventName);
			ModMarioQuaMario.LOGGER.info("Registering sound: {}", id);
			SoundEvent event = SoundEvent.of(id);
			SOUND_EVENTS.put(character, new Pair<>(id, event));
			Registry.register(Registries.SOUND_EVENT, id, event);
		}
	}
	public Identifier getIdentifier(MarioCharacter character) {
		return SOUND_EVENTS.get(character).getLeft();
	}
	public SoundEvent getEvent(MarioCharacter character) {
		return SOUND_EVENTS.get(character).getRight();
	}

	public void broadcast() {
		long seed = RandomSeed.getSeed();
		play(MarioClient.player, seed);
	}
	public void play(PlayerEntity player, long seed) {
		PositionedSoundInstance previousVoiceLine = PLAYER_VOICE_LINES.get(player);
		SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
		soundManager.stop(previousVoiceLine);

		PositionedSoundInstance newSound = new PositionedSoundInstance(
				getEvent(ModMarioQuaMario.getCharacter(player)),
				SoundCategory.PLAYERS,
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

	public static void register() {}
}
