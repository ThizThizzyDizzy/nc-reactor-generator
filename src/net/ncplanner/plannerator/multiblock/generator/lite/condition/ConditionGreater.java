package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
public class ConditionGreater extends BiCondition<Number>{
    public ConditionGreater(){
        super("greater", ConstInt::new);
    }
    @Override
    public String getTitle(){
        return "Greater";
    }
    @Override
    public boolean check(Random rand){
        return v1.get().get().doubleValue()>v2.get().get().doubleValue();
    }
}