package net.ncplanner.plannerator.planner.dssl.object;
public class StackModule extends StackObject{
    private final String value;
    public StackModule(String value){
        this.value = value;
    }
    @Override
    public Type getType(){
        return Type.MODULE;
    }
    @Override
    public String getValue(){
        return value;
    }
    @Override
    public String toString(){
        return "MODULE{"+value+"}";
    }
    @Override
    public StackObject duplicate(){
        return new StackModule(value);
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asModule();
    }
}