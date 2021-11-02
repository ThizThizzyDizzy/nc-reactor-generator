package net.ncplanner.plannerator.planner.s_tack.object;
public abstract class StackNumber extends StackObject{
    protected final Number value;
    public StackNumber(Number value){
        this.value = value;
    }
    @Override
    public Number getValue(){
        return value;
    }
    @Override
    public String toString(){
        return value.toString();
    }
}