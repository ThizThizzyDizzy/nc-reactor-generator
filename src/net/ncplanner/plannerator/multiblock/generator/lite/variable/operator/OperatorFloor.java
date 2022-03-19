package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
public class OperatorFloor extends VariableInt implements Operator{
    public SettingVariable<Number> v = new SettingVariable(null, new ConstInt(0));
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
}