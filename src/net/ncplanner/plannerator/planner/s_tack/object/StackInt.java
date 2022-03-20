package net.ncplanner.plannerator.planner.s_tack.object;
public class StackInt extends StackNumber{
    public StackInt(long value){
        super(value);
    }
    @Override
    public Type getType(){
        return Type.INT;
    }
    @Override
    public Long getValue(){
        return value.longValue();
    }
    @Override
    public StackObject duplicate(){
        return new StackInt(value.longValue());
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asInt();
    }
}