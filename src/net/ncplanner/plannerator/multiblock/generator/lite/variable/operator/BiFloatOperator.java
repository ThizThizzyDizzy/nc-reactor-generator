package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class BiFloatOperator extends VariableFloat implements Operator{
    public SettingVariable<Number> v1 = new SettingVariable(null, new ConstInt(0));
    public SettingVariable<Number> v2 = new SettingVariable(null, new ConstInt(0));
    private final String type;
    private boolean expanded;
    public BiFloatOperator(String type, String name){
        super(name);
        this.type = type;
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
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setVariable("v1", v1);
        ncpf.setVariable("v2", v2);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        ncpf.getVariable("v1", v1);
        ncpf.getVariable("v2", v2);
    }
    @Override
    public final String getType(){
        return type;
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