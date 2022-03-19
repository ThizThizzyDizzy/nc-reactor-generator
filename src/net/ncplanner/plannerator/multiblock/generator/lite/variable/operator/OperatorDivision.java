package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
public class OperatorDivision extends VariableFloat implements Operator{
    public SettingVariable<Number> v1 = new SettingVariable(null, new ConstInt(0));
    public SettingVariable<Number> v2 = new SettingVariable(null, new ConstInt(0));
    public OperatorDivision(){
        super("Division");
    }
    @Override
    public float getValue(){
        return v1.get().get().floatValue()/v2.get().get().floatValue();
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