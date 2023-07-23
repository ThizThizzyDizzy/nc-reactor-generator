package net.ncplanner.plannerator.multiblock.generator.lite.condition;
import java.util.Objects;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNull;
public class ConditionEqual extends BiCondition{
    public ConditionEqual(){
        super("equal", VariableNull::new);
    }
    @Override
    public String getTitle(){
        return "Equal";
    }
    @Override
    public boolean check(Random rand){
        return Objects.equals(v1.get().get(), v2.get().get());
    }
}