package fqf.qua_mario;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModQuakeMovement implements ClientModInitializer {
    public static final String MODID = "squake";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ModConfig CONFIG;
    public static final String CATEGORY = "fabric.mods." + MODID;
    private static final KeyBinding toggleEnabled = new KeyBinding(CATEGORY + "." + "enable", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

    static {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleEnabled);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleEnabled.wasPressed()) {
                ModQuakeMovement.CONFIG.setEnabled(!ModQuakeMovement.CONFIG.isEnabled());
                Text message = ModQuakeMovement.CONFIG.isEnabled() ? Text.translatable("squake.key.toggle.enabled") : Text.translatable("squake.key.toggle.disabled");
                client.player.sendMessage(message, true);
            }
        });
    }

    public static void drawSpeedometer(DrawContext context) {
        if (CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.OFF) {
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

        if (CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_RIGHT || CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_LEFT) {
            textY = context.getScaledWindowHeight() - textRenderer.fontHeight - 2;
        }
        if (CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.TOP_RIGHT || CONFIG.getSpeedometerPosition() == ModConfig.SpeedometerPosition.BOTTOM_RIGHT) {
            textX = context.getScaledWindowWidth() - textRenderer.getWidth(speedStr) - 2;
        }

        context.drawText(textRenderer, speedStr, textX, textY, 0xFFFFFFFF, true);
    }
}