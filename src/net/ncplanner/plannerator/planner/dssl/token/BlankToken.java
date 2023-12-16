package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class BlankToken extends Token{
    public BlankToken(){
        super(separator+"+", true);
    }
    @Override
    public Token newInstance(){
        return new BlankToken();
    }
}