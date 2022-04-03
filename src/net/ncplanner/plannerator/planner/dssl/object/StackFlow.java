package net.ncplanner.plannerator.planner.dssl.object;
public class StackFlow extends StackObject{
    public final Flow type;
    public StackFlow(Flow type){
        this.type = type;
    }
    @Override
    public Type getType(){
        return Type.FLOW;
    }
    @Override
    public Object getValue(){
        return null;
    }
    @Override
    public StackObject duplicate(){
        return new StackFlow(type);
    }
    public static enum Flow{
        CONTINUE,BREAK;
    }
}