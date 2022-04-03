package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
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
        script.push(script.variables.get(text));
    }
}