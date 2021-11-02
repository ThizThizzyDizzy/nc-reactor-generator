package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackFloat;
import static net.ncplanner.plannerator.planner.s_tack.token.Helpers.*;
public class FloatValueToken extends Token{
    public float value;
    public FloatValueToken(){
        super(sign+"?(?>"+digit+"+\\.(?>"+digit+"*)?|\\."+digit+"+)");
    }
    @Override
    public Token newInstance(){
        return new FloatValueToken();
    }
    @Override
    public void load(){
        value = Float.parseFloat(text);
    }
    @Override
    public void run(Script script){
        script.stack.push(new StackFloat(value));
    }
}