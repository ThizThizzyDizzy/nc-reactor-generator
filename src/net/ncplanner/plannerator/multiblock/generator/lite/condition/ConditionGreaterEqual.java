package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
public class ConditionGreaterEqual extends BiCondition<Number>{
    public ConditionGreaterEqual(){
        super("greater_or_equal", ConstInt::new);
    }
    @Override
    public String getTitle(){
        return "Greater or Equal";
    }
    @Override
    public boolean check(Random rand){
        return v1.get().get().doubleValue()>=v2.get().get().doubleValue();
    }
}