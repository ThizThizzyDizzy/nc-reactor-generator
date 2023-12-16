package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class IdentifierToken extends Token{
    public IdentifierToken(){
        super(name);
    }
    @Override
    public Token newInstance(){
        return new IdentifierToken();
    }
}