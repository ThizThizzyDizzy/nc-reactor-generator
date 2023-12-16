package net.ncplanner.plannerator.planner.dssl.token;
public class RBracketToken extends Token{
    public RBracketToken(){
        super("]");
    }
    @Override
    public Token newInstance(){
        return new RBracketToken();
    }
}