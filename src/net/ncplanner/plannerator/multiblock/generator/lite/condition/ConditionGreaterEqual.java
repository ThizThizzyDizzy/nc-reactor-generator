package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
public class ConditionGreaterEqual extends Condition{
    public SettingVariable<Number> v1 = new SettingVariable(null, new ConstInt(0));
    public SettingVariable<Number> v2 = new SettingVariable(null, new ConstInt(0));
    @Override
    public String getTitle(){
        return "Greater or Equal";
    }
    @Override
    public String getTooltip(){
        return null;
    }
    @Override
    public boolean check(Random rand){
        return v1.get().get().doubleValue()>=v2.get().get().doubleValue();
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