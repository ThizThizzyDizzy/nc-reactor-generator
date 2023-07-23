package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorSubtraction;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class Priority<T extends LiteMultiblock> extends DefinedNCPFObject implements ThingWithSettings, ThingWithVariables{
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public ArrayList<Condition> conditions = new ArrayList<>();
    public SettingVariable<Float> operator = new SettingVariable<>("Value", new OperatorSubtraction());
    public long hits;
    @Override
    public int getVariableCount(){
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        return vars[i];
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return operator;
    }
    public void reset(){
        hits = 0;
        for(Condition condition : conditions){
            condition.reset();
        }
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        conditions = ncpf.getRegisteredNCPFList("conditions", Condition.registeredConditions);
        ncpf.getVariable("operator", operator);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setRegisteredNCPFList("conditions", conditions);
        ncpf.setVariable("operator", operator);
    }
}