package fqf.qua_mario;

import fqf.qua_mario.characters.MarioCharacter;
import fqf.qua_mario.characters.characters.CharaMario;
import fqf.qua_mario.powerups.PowerUp;
import fqf.qua_mario.powerups.forms.SuperForm;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.compress.utils.Lists;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModMarioQuaMarioClient implements ClientModInitializer {
	public static final String CATEGORY = "fabric.mods." + ModMarioQuaMario.MOD_ID;
	public static final KeyBinding spinBinding = new KeyBinding(CATEGORY + "." + "spin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);

	public static class BasicPlayerInfo {
		public boolean isMario;
		public MarioCharacter character;
		public PowerUp powerUp;
		private BasicPlayerInfo(boolean isMario, MarioCharacter character, PowerUp powerUp) {
			this.isMario = isMario;
			this.character = character;
			this.powerUp = powerUp;
		}
	}

	public static final List<Entity> SQUASHED_ENTITIES = Lists.newArrayList();
	public static final Map<PlayerEntity, BasicPlayerInfo> OTHER_PLAYER_DATA = new HashMap<>();

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(spinBinding);

		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			ModMarioQuaMario.LOGGER.info("Unloaded entity!");
			SQUASHED_ENTITIES.remove(entity);
		});

		BasicPlayerInfo testasaur = new BasicPlayerInfo(true, CharaMario.INSTANCE, SuperForm.INSTANCE);
//		testasaur.isMario = false;

		MarioPackets.registerClient();
	}

	private record DumbGarbage(DrawContext context, int textX, int textY) {
		public static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	}

	public static void drawScreenInfo(DrawContext context) {
		PlayerEntity player = MinecraftClient.getInstance().player;

		Vec3d pos = player.getPos();
		double dx = pos.x - player.lastRenderX;
		double dz = pos.z - player.lastRenderZ;

		DumbGarbage garbo = new DumbGarbage(context,context.getScaledWindowHeight() - (4 * DumbGarbage.textRenderer.fontHeight) - 2, context.getScaledWindowWidth() - 2);

		drawString(garbo, 0, String.format("Speed: %.2f", Math.sqrt(dx * dx + dz * dz)));
		drawString(garbo, 1, "State: " + MarioClient.getState().getName());
		drawString(garbo, 2, "Cam: " + MarioClient.getCameraAnim());
		drawString(garbo, 3, "Accel: " + MarioClient.lastUsedAccelStat);
	}

	private static void drawString(DumbGarbage garbo, int index, String toDraw) {
		garbo.context.drawText(DumbGarbage.textRenderer, toDraw, garbo.textX - DumbGarbage.textRenderer.getWidth(toDraw), garbo.textY + DumbGarbage.textRenderer.fontHeight * index, 0xFFFFFFFF, true);
	}
}