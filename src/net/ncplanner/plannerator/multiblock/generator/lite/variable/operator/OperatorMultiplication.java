package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
public class OperatorMultiplication extends BiFloatOperator{
    public OperatorMultiplication(){
        super("multiply", "Multiply");
    }
    @Override
    public float getValue(){
        return v1.get().get().floatValue()*v2.get().get().floatValue();
    }
}