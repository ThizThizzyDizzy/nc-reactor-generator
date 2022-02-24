package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNumber;
public class SettingFloat extends VariableNumber<Float> implements Setting<Float>{
    private final String name;
    private float value = 0;
    public SettingFloat(String name, float value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Float get(){
        return value;
    }
    @Override
    public void set(Float value){
        this.value = value;
    }
}