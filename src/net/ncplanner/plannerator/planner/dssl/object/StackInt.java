package net.ncplanner.plannerator.planner.dssl.object;
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
        StackObject bas = obj.getBaseObject();
        if(bas instanceof StackString){
            return new StackInt(Integer.parseInt(((StackString)bas).getValue()));
        }
        return obj.asInt();
    }
}