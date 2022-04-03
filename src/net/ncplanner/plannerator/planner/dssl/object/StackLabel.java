package net.ncplanner.plannerator.planner.dssl.object;
public class StackLabel extends StackObject{
    private final String value;
    public StackLabel(String value){
        this.value = value;
    }
    @Override
    public Type getType(){
        return Type.LABEL;
    }
    @Override
    public String getValue(){
        return value;
    }
    @Override
    public String toString(){
        return "LABEL{"+value+"}";
    }
    @Override
    public StackObject duplicate(){
        return new StackLabel(value);
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asLabel();
    }
}