package net.ncplanner.plannerator.planner.dssl.token;
public class RBraceToken extends Token{
    public RBraceToken(){
        super("}");
    }
    @Override
    public Token newInstance(){
        return new RBraceToken();
    }
}