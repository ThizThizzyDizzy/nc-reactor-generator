package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
public class OperatorMinimum extends BiFloatOperator{
    public OperatorMinimum(){
        super("min", "Minimum");
    }
    @Override
    public float getValue(){
        return Math.min(v1.get().get().floatValue(),v2.get().get().floatValue());
    }
}