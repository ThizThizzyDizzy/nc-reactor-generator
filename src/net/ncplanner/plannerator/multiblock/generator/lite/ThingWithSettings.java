package net.ncplanner.plannerator.multiblock.generator.lite;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
public interface ThingWithSettings extends Expandable{
    public int getSettingCount();
    public Setting getSetting(int i);
    public String getSettingsPrefix();
}