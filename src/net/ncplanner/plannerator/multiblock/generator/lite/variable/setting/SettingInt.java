package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNumber;
public class SettingInt extends VariableNumber<Integer> implements Setting<Integer>{
    private final String name;
    private int value = 0;
    public SettingInt(String name, int value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Integer get(){
        return value;
    }
    @Override
    public void set(Integer value){
        this.value = value;
    }
}