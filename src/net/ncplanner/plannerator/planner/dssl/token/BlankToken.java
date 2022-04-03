package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class BlankToken extends Token{
    public BlankToken(){
        super(separator+"+", true);
    }
    @Override
    public Token newInstance(){
        return new BlankToken();
    }
    @Override
    public void run(Script script){}
}