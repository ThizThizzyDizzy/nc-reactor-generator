package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackRBracket;
public class RBracketToken extends Token{
    public RBracketToken(){
        super("]");
    }
    @Override
    public Token newInstance(){
        return new RBracketToken();
    }
    @Override
    public void run(Script script){
        script.push(new StackRBracket());
    }
}