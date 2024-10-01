package fqf.qua_mario;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ModMarioQuaMarioClient implements ClientModInitializer {
	public static final String CATEGORY = "fabric.mods." + ModMarioQuaMario.MOD_ID;
	public static final KeyBinding spinBinding = new KeyBinding(CATEGORY + "." + "spin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(spinBinding);

//		PayloadTypeRegistry.playC2S().register(ModMarioQuaMario.PlayJumpSfxPayload.ID, ModMarioQuaMario.PlayJumpSfxPayload.CODEC);
//		PayloadTypeRegistry.playC2S().register(StompAttack.requestStompPayload.ID, StompAttack.requestStompPayload.CODEC);

		ClientPlayNetworking.registerGlobalReceiver(ModMarioQuaMario.SetMarioEnabledPayload.ID, (payload, context) -> {
			ModMarioQuaMario.LOGGER.info("Received the packet client-side! " + payload.isMario());
			MarioClient.isMario = payload.isMario();
		});
	}

	public static void drawSpeedometer(DrawContext context) {
		if (ModMarioQuaMario.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.OFF) {
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		Vec3d pos = player.getPos();
		TextRenderer textRenderer = client.textRenderer;

		double dx = pos.x - player.lastRenderX;
		double dz = pos.z - player.lastRenderZ;
		String speedStr = String.format("Speed: %.2f", Math.sqrt(dx * dx + dz * dz) * 20);
		String stateStr = "State: " + MarioClient.getState().name;
		String animStr = "Camnim: " + MarioClient.getCameraAnim();
		String accelStr = "Accel: " + MarioClient.lastUsedAccelStat;

		int textX = 2;
		int textY = 2;

		if (ModMarioQuaMario.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_RIGHT || ModMarioQuaMario.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_LEFT) {
			textY = context.getScaledWindowHeight() - (4 * textRenderer.fontHeight) - 2;
		}
		if (ModMarioQuaMario.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.TOP_RIGHT || ModMarioQuaMario.CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_RIGHT) {
			int widestWidth = Math.max(textRenderer.getWidth(speedStr), Math.max(textRenderer.getWidth(stateStr), Math.max(textRenderer.getWidth(animStr), textRenderer.getWidth(accelStr))));

			textX = context.getScaledWindowWidth() - widestWidth - 2;
		}

		context.drawText(textRenderer, speedStr, textX, textY, 0xFFFFFFFF, true);
		context.drawText(textRenderer, stateStr, textX, textY + textRenderer.fontHeight, 0xFFFFFFFF, true);
		context.drawText(textRenderer, animStr, textX, textY + 2 * textRenderer.fontHeight, 0xFFFFFFFF, true);
		context.drawText(textRenderer, accelStr, textX, textY + 3 * textRenderer.fontHeight, 0xFFFFFFFF, true);
	}
}