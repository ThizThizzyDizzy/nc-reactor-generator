package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import static net.ncplanner.plannerator.planner.s_tack.token.Helpers.*;
public class IdentifierToken extends Token{
    public IdentifierToken(){
        super(name);
    }
    @Override
    public Token newInstance(){
        return new IdentifierToken();
    }
    @Override
    public void run(Script script){
        script.stack.push(script.variables.get(text));
    }
}