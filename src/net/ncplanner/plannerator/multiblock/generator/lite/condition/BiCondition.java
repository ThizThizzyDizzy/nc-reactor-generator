package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class BiCondition<T> extends Condition{
    public SettingVariable<T> v1;
    public SettingVariable<T> v2;
    public BiCondition(String name, Supplier<Variable> initialValue){
        super(name);
        v1 = new SettingVariable(null, initialValue.get());
        v2 = new SettingVariable(null, initialValue.get());
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
    public void convertFromObject(NCPFObject ncpf){
        ncpf.getVariable("v1", v1);
        ncpf.getVariable("v2", v2);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setVariable("v1", v1);
        ncpf.setVariable("v2", v2);
    }
}