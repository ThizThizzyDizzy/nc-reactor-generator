package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingConditionList;
public class ConditionOr extends Condition{
    public SettingConditionList conditions = new SettingConditionList(null, new ArrayList<>());
    @Override
    public String getTitle(){
        return "Or";
    }
    @Override
    public String getTooltip(){
        return null;
    }
    @Override
    public boolean check(Random rand){
        for(Condition condition : conditions.get()){
            condition.hits++;
            if(condition.check(rand))return true;
        }
        return false;
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return conditions;
    }
}