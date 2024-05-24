package net.ncplanner.plannerator.planner.dssl.token;
public class LBraceToken extends Token{
    public LBraceToken(){
        super("\\{");
    }
    @Override
    public Token newInstance(){
        return new LBraceToken();
    }
}