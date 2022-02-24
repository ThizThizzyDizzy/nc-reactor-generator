package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNumber;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
public class ConditionLess extends Condition{
    public VariableNumber v1 = new ConstInt(0);
    public VariableNumber v2 = new ConstInt(0);
    @Override
    public boolean check(Random rand){
        return v1.get().doubleValue()<v2.get().doubleValue();
    }
}