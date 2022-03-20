package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
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