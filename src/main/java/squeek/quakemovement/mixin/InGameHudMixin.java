package squeek.quakemovement.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.quakemovement.ModQuakeMovement;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ModQuakeMovement.drawSpeedometer(context);
    }
}
