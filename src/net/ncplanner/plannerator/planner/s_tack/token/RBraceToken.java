package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class RBraceToken extends Token{
    public RBraceToken(){
        super("}");
    }
    @Override
    public Token newInstance(){
        return new RBraceToken();
    }
    @Override
    public void run(Script script){
        throw new IllegalArgumentException("Found } with no {!");
    }
}