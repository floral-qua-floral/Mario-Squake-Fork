package fqf.qua_mario;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "qua_mario")
public class ModConfig implements ConfigData {
    private boolean enabled = true;



    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private SpeedometerPosition speedometerPosition = SpeedometerPosition.OFF;

    public enum SpeedometerPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        OFF
    }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private SpinputType spinputType = SpinputType.LEFTRIGHT;

    public enum SpinputType {
        LEFTRIGHT,
        KEYBIND,
        EITHER
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    public SpeedometerPosition getSpeedometerPosition() {
        return this.speedometerPosition;
    }

    public SpinputType getSpinputType() {
        return this.spinputType;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}
