package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
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
    @Override
    public void run(Script script){
        script.push(new StackInt(value));
    }
}