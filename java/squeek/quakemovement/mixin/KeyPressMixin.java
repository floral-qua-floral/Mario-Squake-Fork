package squeek.quakemovement.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.quakemovement.KeyBindInitializer;
import squeek.quakemovement.ModQuakeMovement;

@Mixin(MinecraftClient.class)
public class KeyPressMixin {
    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    public void handleInputEvents(CallbackInfo info) {
        if (KeyBindInitializer.ENABLE.wasPressed()) {
            ModQuakeMovement.CONFIG.setEnabled(!ModQuakeMovement.CONFIG.isEnabled());
            MinecraftClient.getInstance().player.sendMessage(ModQuakeMovement.CONFIG.isEnabled() ? Text.translatable("squake.key.toggle.enabled") : Text.translatable("squake.key.toggle.disabled"), true);
        }
    }
}
