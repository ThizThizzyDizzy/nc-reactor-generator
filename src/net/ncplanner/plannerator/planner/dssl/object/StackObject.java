package net.ncplanner.plannerator.planner.dssl.object;
import java.util.Objects;
import net.ncplanner.plannerator.planner.dssl.Script;
public abstract class StackObject{
    public abstract Type getType();
    public abstract Object getValue();
    public Type getBaseType(){
        return getType();
    }
    public Object getBaseValue(){
        return getValue();
    }
    public abstract StackObject duplicate();
    public StackObject getBaseObject(){
        return this;
    }
    public void onUnpack(Script script){
        throw new UnsupportedOperationException(getType().toString()+" can't unpack!");
    }
    public String getTypeString(){
        return getType().toString();
    }
    public StackObject cast(StackObject obj){
        throw new IllegalArgumentException("Can't cast to that!");
    }
    @Override
    public int hashCode(){
        int hash = 7;
        return hash;
    }
    public static enum Type{
        INT,FLOAT,CHAR,BOOL,STRING,FUNCTION,VAR,LABEL,COLLECTION,LBRACKET,RBRACKET,NULL,TYPE,FLOW;
        @Override
        public String toString(){
            return super.toString().toLowerCase();
        }
    }
    public StackInt asInt(){
        return (StackInt)this;
    }
    public StackFloat asFloat(){
        return (StackFloat)this;
    }
    public StackNumber asNumber(){
        return (StackNumber)this;
    }
    public StackChar asChar(){
        return (StackChar)this;
    }
    public StackBool asBool(){
        return (StackBool)this;
    }
    public StackString asString(){
        return (StackString)this;
    }
    public StackMethod asMethod(){
        return (StackMethod)this;
    }
    public StackVariable asVariable(){
        return (StackVariable)this;
    }
    public StackLabel asLabel(){
        return (StackLabel)this;
    }
    public StackCollection asCollection(){
        return (StackCollection)this;
    }
    public StackRange asRange(){
        return (StackRange)this;
    }
    public StackList asList(){
        return (StackList)this;
    }
    public StackTuple asTuple(){
        return (StackTuple)this;
    }
    public StackSet asSet(){
        return (StackSet)this;
    }
    public StackDict asDict(){
        return (StackDict)this;
    }
    public StackType asType(){
        return (StackType)this;
    }
    public StackFlow asFlow(){
        return (StackFlow)this;
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof StackObject){
            return getBaseType().equals(((StackObject)obj).getBaseType())&&Objects.equals(getBaseValue(), ((StackObject)obj).getBaseValue());
        }
        return false;
    }
}