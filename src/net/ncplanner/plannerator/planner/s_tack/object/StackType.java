package net.ncplanner.plannerator.planner.s_tack.object;
public class StackType extends StackObject{
    public final StackObject internal;
    public StackType(StackObject obj){
        this.internal = obj;
    }
    @Override
    public Type getType(){
        return Type.TYPE;
    }
    @Override
    public String getValue(){
        return internal.getTypeString();
    }
    @Override
    public String toString(){
        return "TYPE{"+internal.getTypeString()+"}";
    }
    @Override
    public StackObject duplicate(){
        return new StackType(internal);
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asType();
    }
}