package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithVariables;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.ncpf.RegisteredNCPFObject;
public abstract class Condition extends RegisteredNCPFObject implements ThingWithSettings, ThingWithVariables{
    public static final HashMap<String, Supplier<Condition>> registeredConditions = new HashMap<>();
    public boolean expanded;
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public long hits = 0;
    public Condition(String name){
        super(name);
    }
    public abstract String getTitle();
    public String getTooltip(){
        return null;
    }
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
    @Override
    public boolean isExpanded(){
        return expanded;
    }
    @Override
    public void setExpanded(boolean expanded){
        this.expanded = expanded;
    }
    @Override
    public String getSettingsPrefix(){
        return "Condition";
    }
}