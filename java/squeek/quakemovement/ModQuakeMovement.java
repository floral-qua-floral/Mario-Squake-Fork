package squeek.quakemovement;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class ModQuakeMovement implements ModInitializer {
    public static final ModConfig CONFIG;

    static {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public void onInitialize() {
        //Cause this class to be loaded so the config loads on startup
    }
}