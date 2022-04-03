package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
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