package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackLBracket;
public class LBracketToken extends Token{
    public LBracketToken(){
        super("\\[");
    }
    @Override
    public Token newInstance(){
        return new LBracketToken();
    }
    @Override
    public void run(Script script){
        script.push(new StackLBracket());
    }
}