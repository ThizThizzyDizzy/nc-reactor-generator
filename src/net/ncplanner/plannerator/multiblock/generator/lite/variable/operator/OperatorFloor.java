package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class OperatorFloor extends VariableInt implements Operator{
    public SettingVariable<Number> v = new SettingVariable(null, new ConstInt(0));
    private boolean expanded;
    public OperatorFloor(){
        super("Floor");
    }
    @Override
    public int getValue(){
        return v.get().get().intValue();
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return v;
    }
    @Override
    public String getType(){
        return "floor";
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setVariable("v", v);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        ncpf.getVariable("v", v);
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
        return "Operator";
    }
}