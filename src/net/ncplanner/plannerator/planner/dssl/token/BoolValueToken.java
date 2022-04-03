package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
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
    @Override
    public void run(Script script){
        script.push(new StackBool(value));
    }
}