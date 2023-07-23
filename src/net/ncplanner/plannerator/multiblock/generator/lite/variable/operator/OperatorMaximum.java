package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
public class OperatorMaximum extends BiFloatOperator{
    public OperatorMaximum(){
        super("max", "Maximum");
    }
    @Override
    public float getValue(){
        return Math.max(v1.get().get().floatValue(),v2.get().get().floatValue());
    }
}