package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.ArrayList;
import java.util.Random;
public class ConditionAnd extends Condition{
    public ArrayList<Condition> conditions = new ArrayList<>();
    @Override
    public boolean check(Random rand){
        for(Condition condition : conditions){
            condition.hits++;
            if(!condition.check(rand))return false;
        }
        return true;
    }
}