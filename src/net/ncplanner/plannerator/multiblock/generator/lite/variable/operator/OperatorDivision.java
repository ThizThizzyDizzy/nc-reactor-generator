package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class OperatorDivision extends BiFloatOperator{
    public OperatorDivision(){
        super("divide", "Divide");
    }
    @Override
    public float getValue(){
        return v1.get().get().floatValue()/v2.get().get().floatValue();
    }
}
