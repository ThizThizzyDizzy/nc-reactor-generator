package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Random;
public class ConditionNot extends Condition{
    public Condition condition;
    @Override
    public boolean check(Random rand){
        condition.hits++;
        return !condition.check(rand);
    }
}