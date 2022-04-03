package net.ncplanner.plannerator.planner.dssl.object;
public class StackVariable extends StackObject{
    private final String name;
    private StackObject value;
    public StackVariable(String name, StackObject value){
        this.name = name;
        this.value = value;
    }
    @Override
    public Type getBaseType(){
        return value.getBaseType();
    }
    @Override
    public Object getBaseValue(){
        return value.getBaseValue();
    }
    @Override
    public StackObject getBaseObject(){
        return value;
    }
    @Override
    public Type getType(){
        return Type.VAR;
    }
    @Override
    public StackObject getValue(){
        return value;
    }
    @Override
    public String toString(){
        String s = value.toString();
        if(s.startsWith("{")&&s.endsWith("}"))s = s.substring(1, s.length()-1);
        return name+"{"+s+"}";
    }
    @Override
    public StackBool asBool(){
        return value.asBool();
    }
    @Override
    public StackChar asChar(){
        return value.asChar();
    }
    @Override
    public StackCollection asCollection(){
        return value.asCollection();
    }
    @Override
    public StackFloat asFloat(){
        return value.asFloat();
    }
    @Override
    public StackInt asInt(){
        return value.asInt();
    }
    @Override
    public StackNumber asNumber(){
        return value.asNumber();
    }
    @Override
    public StackLabel asLabel(){
        return value.asLabel();
    }
    @Override
    public StackMethod asMethod(){
        return value.asMethod();
    }
    @Override
    public StackString asString(){
        return value.asString();
    }
    @Override
    public StackObject duplicate(){
        return value.duplicate();
    }
    public void setValue(StackObject newValue){
        value = newValue;
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asVariable();
    }
}