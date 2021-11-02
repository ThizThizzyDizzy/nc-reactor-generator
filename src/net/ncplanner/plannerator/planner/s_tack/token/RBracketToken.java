package net.ncplanner.plannerator.planner.s_tack.token;
public class RBracketToken extends Token{
    public RBracketToken(){
        super("]");
    }
    @Override
    public Token newInstance(){
        return new RBracketToken();
    }
}