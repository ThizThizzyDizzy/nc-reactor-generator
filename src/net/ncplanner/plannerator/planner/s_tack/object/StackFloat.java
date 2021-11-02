package net.ncplanner.plannerator.planner.s_tack.object;
public class StackFloat extends StackNumber{
    public StackFloat(float value){
        super(value);
    }
    @Override
    public Type getType(){
        return Type.FLOAT;
    }
    @Override
    public Float getValue(){
        return value.floatValue();
    }
    @Override
    public StackObject duplicate(){
        return new StackFloat(value.floatValue());
    }
}