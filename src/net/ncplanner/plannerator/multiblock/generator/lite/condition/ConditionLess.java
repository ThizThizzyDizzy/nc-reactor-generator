package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
public class ConditionLess extends BiCondition<Number>{
    public ConditionLess(){
        super("less", ConstInt::new);
    }
    @Override
    public String getTitle(){
        return "Less";
    }
    @Override
    public boolean check(Random rand){
        return v1.get().get().doubleValue()<v2.get().get().doubleValue();
    }
}