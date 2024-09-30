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

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private CameraAnimType sideflipAnimType = CameraAnimType.AUTHENTIC;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private CameraAnimType backflipAnimType = CameraAnimType.AUTHENTIC;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private CameraAnimType tripleJumpAnimType = CameraAnimType.GENTLE;

    public enum CameraAnimType {
        AUTHENTIC,
        GENTLE,
        NO_FUN_ALLOWED
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

    public CameraAnimType getSideflipAnimType() {
        return this.sideflipAnimType;
    }

    public CameraAnimType getBackflipAnimType() {
        return this.backflipAnimType;
    }

    public CameraAnimType getTripleJumpAnimType() {
        return this.tripleJumpAnimType;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
