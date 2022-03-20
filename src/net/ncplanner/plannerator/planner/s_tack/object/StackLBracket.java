package net.ncplanner.plannerator.planner.s_tack.object;
public class StackLBracket extends StackObject{
    @Override
    public Type getType(){
        return Type.LBRACKET;
    }
    @Override
    public Object getValue(){
        return null;
    }
    @Override
    public String toString(){
        return "LBRACKET";
    }
    @Override
    public StackObject duplicate(){
        return new StackLBracket();
    }
}