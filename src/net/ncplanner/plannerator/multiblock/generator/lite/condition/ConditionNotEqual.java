package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Objects;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNull;
public class ConditionNotEqual extends Condition{
    public Variable v1 = new VariableNull();
    public Variable v2 = new VariableNull();
    @Override
    public boolean check(Random rand){
        return !Objects.equals(v1.get(), v2.get());
    }
}