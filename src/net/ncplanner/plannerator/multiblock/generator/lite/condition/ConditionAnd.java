package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingConditionList;
public class ConditionAnd extends Condition{
    public SettingConditionList conditions = new SettingConditionList(null, new ArrayList<>());
    @Override
    public String getTitle(){
        return "And";
    }
    @Override
    public String getTooltip(){
        return null;
    }
    @Override
    public boolean check(Random rand){
        for(Condition condition : conditions.get()){
            condition.hits++;
            if(!condition.check(rand))return false;
        }
        return true;
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return conditions;
    }
    @Override
    public void getAllVariables(ArrayList<Variable> vars, ArrayList<String> names, String prevPath){
        super.getAllVariables(vars, names, prevPath);
        ArrayList<Condition> lst = conditions.get();
        for(int i = 0; i<lst.size(); i++){
            Condition condition = lst.get(i);
            condition.getAllVariables(vars, names, prevPath+".conditions["+i+"]{Condition "+(i+1)+" ("+condition.getTitle()+")}");
        }
    }
}