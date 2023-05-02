package squeek.quakemovement.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface ILivingEntityMixin {
    @Invoker
    public float invokeGetBaseMovementSpeedMultiplier();
}
