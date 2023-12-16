package net.ncplanner.plannerator.planner.dssl.token;
public class IntValueToken extends Token{
    public long value;
    public IntValueToken(){
        super("(?>\\-[0-9][0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-8]?|[+-]?[0-9][0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-7]?)");//(sign+"?"+digit+"+")
    }
    @Override
    public Token newInstance(){
        return new IntValueToken();
    }
    @Override
    public void load(){
        value = Long.parseLong(text);
    }
}