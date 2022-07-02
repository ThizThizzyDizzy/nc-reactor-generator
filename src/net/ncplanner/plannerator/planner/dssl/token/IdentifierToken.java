package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
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
        StackVariable var = script.variables.get(text);
        if(var==null)throw new NullPointerException("S'tack variable "+text+" does not exist!");
        script.push(var);
    }
}