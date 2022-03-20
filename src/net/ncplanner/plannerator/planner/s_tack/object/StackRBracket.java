package net.ncplanner.plannerator.planner.s_tack.object;
public class StackRBracket extends StackObject{
    @Override
    public Type getType(){
        return Type.RBRACKET;
    }
    @Override
    public Object getValue(){
        return null;
    }
    @Override
    public String toString(){
        return "RBRACKET";
    }
    @Override
    public StackObject duplicate(){
        return new StackRBracket();
    }
}