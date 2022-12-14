package net.ncplanner.plannerator.planner.dssl.object;

import java.util.ArrayList;
import java.util.Arrays;

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
        if(obj instanceof StackChar)return new StackString(obj.toString());
        if(obj instanceof StackNumber)return new StackString(obj.toString());
        return obj.asString();
    }
    @Override
    public StackTuple asTuple() {
        char[] chars = value.toCharArray();
        ArrayList<StackObject> chrs = new ArrayList<>();
        for(char c : chars)chrs.add(new StackChar(c));
        return new StackTuple(chrs);
    }
    
}