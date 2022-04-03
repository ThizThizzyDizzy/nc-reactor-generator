package net.ncplanner.plannerator.planner.dssl.token;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackCollection;
import net.ncplanner.plannerator.planner.dssl.object.StackLBracket;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
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