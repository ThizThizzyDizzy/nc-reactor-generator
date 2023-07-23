package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
public class OperatorDivision extends BiFloatOperator{
    public OperatorDivision(){
        super("divide", "Divide");
    }
    @Override
    public float getValue(){
        return v1.get().get().floatValue()/v2.get().get().floatValue();
    }
}