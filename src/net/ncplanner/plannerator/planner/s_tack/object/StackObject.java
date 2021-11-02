package net.ncplanner.plannerator.planner.s_tack.object;
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
    public static enum Type{
        INT,FLOAT,CHAR,BOOL,STRING,FUNCTION,VAR,LABEL,COLLECTION;
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
}