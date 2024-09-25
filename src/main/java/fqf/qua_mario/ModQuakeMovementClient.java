package fqf.qua_mario;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ModQuakeMovementClient implements ClientModInitializer {
	public static final String CATEGORY = "fabric.mods." + ModQuakeMovement.MOD_ID;
	private static final KeyBinding toggleEnabled = new KeyBinding(CATEGORY + "." + "enable", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
	private static final KeyBinding spin = new KeyBinding(CATEGORY + "." + "spin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(toggleEnabled);
		KeyBindingHelper.registerKeyBinding(spin);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleEnabled.wasPressed()) {
				ModQuakeMovement.CONFIG.setEnabled(!ModQuakeMovement.CONFIG.isEnabled());
				Text message = ModQuakeMovement.CONFIG.isEnabled() ? Text.translatable("squake.key.toggle.enabled") : Text.translatable("squake.key.toggle.disabled");
				assert client.player != null;
				client.player.sendMessage(message, true);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(ModQuakeMovement.SetMarioEnabledPayload.ID, (payload, context) -> {
			ModQuakeMovement.LOGGER.info("Received the packet client-side! " + payload.isMario());
			MarioClient.isMario = payload.isMario();
		});
	}

	public static void drawSpeedometer(DrawContext context) {
		if (ModQuakeMovement.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.OFF) {
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		Vec3d pos = player.getPos();
		TextRenderer textRenderer = client.textRenderer;

		double dx = pos.x - player.lastRenderX;
		double dz = pos.z - player.lastRenderZ;
		String speedStr = String.format("Speed: %.2f", Math.sqrt(dx * dx + dz * dz) * 20);

		int textX = 2;
		int textY = 2;

		if (ModQuakeMovement.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_RIGHT || ModQuakeMovement.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_LEFT) {
			textY = context.getScaledWindowHeight() - textRenderer.fontHeight - 2;
		}
		if (ModQuakeMovement.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.TOP_RIGHT || ModQuakeMovement.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_RIGHT) {
			textX = context.getScaledWindowWidth() - textRenderer.getWidth(speedStr) - 2;
		}

		context.drawText(textRenderer, speedStr, textX, textY, 0xFFFFFFFF, true);
	}
}