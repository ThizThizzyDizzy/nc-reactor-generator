package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
public class OperatorAddition extends BiFloatOperator{
    public OperatorAddition(){
        super("add", "Add");
    }
    @Override
    public float getValue(){
        return v1.get().get().floatValue()+v2.get().get().floatValue();
    }
}