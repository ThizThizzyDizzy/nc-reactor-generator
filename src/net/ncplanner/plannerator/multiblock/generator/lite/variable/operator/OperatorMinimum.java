package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
public class OperatorMinimum extends VariableFloat implements Operator{
    public SettingVariable<Number> v1 = new SettingVariable(null, new ConstInt(0));
    public SettingVariable<Number> v2 = new SettingVariable(null, new ConstInt(0));
    public OperatorMinimum(){
        super("Minimum");
    }
    @Override
    public float getValue(){
        return Math.min(v1.get().get().floatValue(),v2.get().get().floatValue());
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