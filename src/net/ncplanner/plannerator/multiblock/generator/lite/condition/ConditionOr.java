package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
public class ConditionOr extends Condition{
    public ArrayList<Condition> conditions = new ArrayList<>();
    @Override
    public boolean check(Random rand){
        for(Condition condition : conditions){
            condition.hits++;
            if(condition.check(rand))return true;
        }
        return false;
    }
}