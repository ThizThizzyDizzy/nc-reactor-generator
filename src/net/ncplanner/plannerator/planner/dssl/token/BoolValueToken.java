package net.ncplanner.plannerator.planner.dssl.token;
public class BoolValueToken extends Token{
    public boolean value;
    public BoolValueToken(){
        super("(?>true|false)");
    }
    @Override
    public Token newInstance(){
        return new BoolValueToken();
    }
    @Override
    public void load(){
        value = Boolean.parseBoolean(text);
    }
}