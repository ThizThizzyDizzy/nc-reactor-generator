package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Objects;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNull;
public class ConditionNotEqual extends BiCondition{
    public ConditionNotEqual(){
        super("not_equal", VariableNull::new);
    }
    @Override
    public String getTitle(){
        return "Not Equal";
    }
    @Override
    public boolean check(Random rand){
        return !Objects.equals(v1.get().get(), v2.get().get());
    }
}