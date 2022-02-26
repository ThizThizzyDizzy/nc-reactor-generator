package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Objects;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNull;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
public class ConditionEqual extends Condition{
    public SettingVariable v1 = new SettingVariable(null, new VariableNull());
    public SettingVariable v2 = new SettingVariable(null, new VariableNull());
    @Override
    public String getTitle(){
        return "Equal";
    }
    @Override
    public String getTooltip(){
        return null;
    }
    @Override
    public boolean check(Random rand){
        return Objects.equals(v1.get().get(), v2.get().get());
    }
    @Override
    public int getSettingCount(){
        return 2;
    }
    @Override
    public Setting getSetting(int i){
        switch(i){
            case 0:
                return v1;
            case 1:
                return v2;
        }
        return null;
    }
}