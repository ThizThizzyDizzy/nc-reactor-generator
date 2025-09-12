package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class OperatorMinimum extends BiFloatOperator{
    public OperatorMinimum(){
        super("min", "Minimum");
    }
    @Override
    public float getValue(){
        return Math.min(v1.get().get().floatValue(), v2.get().get().floatValue());
    }
}
