package net.ncplanner.plannerator.planner.s_tack.object;
public class StackInt extends StackNumber{
    public StackInt(int value){
        super(value);
    }
    @Override
    public Type getType(){
        return Type.INT;
    }
    @Override
    public Integer getValue(){
        return value.intValue();
    }
    @Override
    public StackObject duplicate(){
        return new StackInt(value.intValue());
    }
}