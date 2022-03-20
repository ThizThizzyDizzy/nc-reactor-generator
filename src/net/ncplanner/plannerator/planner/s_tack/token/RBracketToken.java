package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackRBracket;
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