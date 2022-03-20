package net.ncplanner.plannerator.planner.s_tack.object;
public class StackString extends StackObject{
    private final String value;
    public StackString(String value){
        this.value = value;
    }
    @Override
    public Type getType(){
        return Type.STRING;
    }
    @Override
    public String getValue(){
        return value;
    }
    @Override
    public String toString(){
        return "\""+value+"\"";
    }
    @Override
    public StackObject duplicate(){
        return new StackString(value);
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asString();
    }
}