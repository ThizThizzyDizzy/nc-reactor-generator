package net.ncplanner.plannerator.planner.dssl.token;
public class LBracketToken extends Token{
    public LBracketToken(){
        super("\\[");
    }
    @Override
    public Token newInstance(){
        return new LBracketToken();
    }
}