package net.ncplanner.plannerator.planner.dssl.token;
public class IncrementToken extends Token{
    public IncrementToken(){
        super("\\+\\+");
    }
    @Override
    public Token newInstance(){
        return new IncrementToken();
    }
}