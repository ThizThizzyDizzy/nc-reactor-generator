package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
public class ConstInt extends VariableInt{
    public ConstInt(int value){
        super("Constant");
        this.value = value;
    }
    public int value;
    @Override
    public int getValue(){
        return value;
    }
}