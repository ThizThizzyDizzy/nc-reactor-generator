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
        value = Boolean.valueOf(text);
    }
    @Override
    public void run(Script script){
        script.stack.push(new StackBool(value));
    }
}