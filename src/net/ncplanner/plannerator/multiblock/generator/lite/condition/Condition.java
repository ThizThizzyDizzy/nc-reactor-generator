package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithVariables;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
public abstract class Condition implements ThingWithSettings, ThingWithVariables{
    public static final ArrayList<Supplier<Condition>> conditions = new ArrayList<>();
    static{
        conditions.add(ConditionEqual::new);
        conditions.add(ConditionNotEqual::new);
        conditions.add(ConditionLess::new);
        conditions.add(ConditionGreater::new);
        conditions.add(ConditionLessEqual::new);
        conditions.add(ConditionGreaterEqual::new);
        conditions.add(ConditionAnd::new);
        conditions.add(ConditionOr::new);
        conditions.add(ConditionNot::new);
    }
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public long hits = 0;
    public abstract String getTitle();
    public abstract String getTooltip();
    public abstract boolean check(Random rand);
    @Override
    public int getVariableCount(){
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        return vars[i];
    }
    public void getAllVariables(ArrayList<Variable> vars, ArrayList<String> names, String prevPath){
        for(int i = 0; i<getVariableCount(); i++){
            Variable v = getVariable(i);
            vars.add(v);names.add(prevPath+"."+v.getName());
        }
    }
    public void reset(){
        hits = 0;
    }
}