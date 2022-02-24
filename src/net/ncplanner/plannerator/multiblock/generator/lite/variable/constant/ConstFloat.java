package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
public class ConstFloat extends VariableFloat{
    public ConstFloat(float value){
        super("Constant");
        this.value = value;
    }
    public float value;
    @Override
    public float getValue(){
        return value;
    }
}