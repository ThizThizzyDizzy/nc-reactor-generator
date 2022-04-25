package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingCondition;
public class ConditionNot extends Condition{
    public SettingCondition condition = new SettingCondition(null, null);
    @Override
    public boolean check(Random rand){
        Condition c = condition.get();
        c.hits++;
        return !c.check(rand);
    }
    @Override
    public String getTitle(){
        return "Not";
    }
    @Override
    public String getTooltip(){
        return null;
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return condition;
    }
    @Override
    public void getAllVariables(ArrayList<Variable> vars, ArrayList<String> names, String prevPath){
        super.getAllVariables(vars, names, prevPath);
        Condition c = condition.get();
        if(c!=null)c.getAllVariables(vars, names, prevPath+".condition{Condition ("+c.getTitle()+")}");
    }
    @Override
    public void reset(){
        super.reset();
        condition.get().reset();
    }
}