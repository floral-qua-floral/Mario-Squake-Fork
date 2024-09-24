package fqf.qua_mario;

import net.minecraft.client.network.ClientPlayerEntity;

import java.util.EnumMap;

public class MarioInputs {
	public enum Key {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		JUMP,
		SNEAK,

		SPIN
	}

	private static final EnumMap<Key, Boolean> HELD = new EnumMap<>(Key.class);
	private static final EnumMap<Key, Integer> PRESSED_BUFFERS = new EnumMap<>(Key.class);

	private static final int BUFFER_LENGTH = 6;

	public static void update(ClientPlayerEntity player) {
		update(Key.FORWARD, player.input.pressingForward);
		update(Key.BACKWARD, player.input.pressingBack);
		update(Key.LEFT, player.input.pressingLeft);
		update(Key.RIGHT, player.input.pressingRight);

		update(Key.JUMP, player.input.jumping);
		update(Key.SNEAK, player.input.sneaking);

		HELD.put(Key.SPIN, isHeld(Key.LEFT) && isHeld(Key.RIGHT));
		if(isPressed(Key.LEFT, false) && isPressed(Key.RIGHT, false) && isHeld(Key.LEFT) && isHeld(Key.RIGHT))
			PRESSED_BUFFERS.put(Key.SPIN, BUFFER_LENGTH);
		else
			decrementBuffer(Key.SPIN);
	}

	private static void update(Key key, boolean keyDown) {
		if (keyDown && HELD.get(key) != null && !HELD.get(key))
			PRESSED_BUFFERS.put(key, BUFFER_LENGTH);
		else
			decrementBuffer(key);

		HELD.put(key, keyDown);
	}
	private static void decrementBuffer(Key key) {
		Integer bufferValue = PRESSED_BUFFERS.get(key);
		if (bufferValue == null)
			PRESSED_BUFFERS.put(key, 0);
		else if (bufferValue > 0)
			PRESSED_BUFFERS.put(key, bufferValue - 1);
	}

	public static boolean isHeld(Key input) {
		return HELD.get(input);
	}

	public static boolean isPressed(Key input) { return isPressed(input, false); }

	public static void unbuffer(Key input) {
		PRESSED_BUFFERS.put(input, 0);
	}

	public static boolean isPressed(Key input, boolean unbuffer) {
		boolean keyPressed = PRESSED_BUFFERS.get(input) > 0;
		if(keyPressed && unbuffer) unbuffer(input);
		return keyPressed;
	}
}
