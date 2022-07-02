package net.ncplanner.plannerator.planner.dssl.object;
public class StackNull extends StackObject{
    public static final StackNull INSTANCE = new StackNull();
    private StackNull(){}
    @Override
    public Type getType(){
        return Type.NULL;
    }
    @Override
    public String toString(){
        return "null";
    }
    @Override
    public Object getValue(){
        return null;
    }
    @Override
    public StackObject duplicate(){
        return this;
    }
}