package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
public class OperatorSubtraction extends BiFloatOperator{
    public OperatorSubtraction(){
        super("subtract", "Subtract");
    }
    @Override
    public float getValue(){
        return v1.get().get().floatValue()-v2.get().get().floatValue();
    }
}