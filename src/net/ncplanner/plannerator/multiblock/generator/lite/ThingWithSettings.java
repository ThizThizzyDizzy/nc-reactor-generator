package net.ncplanner.plannerator.multiblock.generator.lite;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
public interface ThingWithSettings{
    public int getSettingCount();
    public Setting getSetting(int i);
}