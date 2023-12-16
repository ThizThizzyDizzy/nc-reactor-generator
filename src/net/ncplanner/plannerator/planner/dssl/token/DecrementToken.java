package net.ncplanner.plannerator.planner.dssl.token;
public class DecrementToken extends Token{
    public DecrementToken(){
        super("\\-\\-");
    }
    @Override
    public Token newInstance(){
        return new DecrementToken();
    }
}