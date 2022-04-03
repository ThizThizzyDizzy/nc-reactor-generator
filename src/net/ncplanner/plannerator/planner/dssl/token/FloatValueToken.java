package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackFloat;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class FloatValueToken extends Token{
    public double value;
    public FloatValueToken(){
        super(sign+"?(?>"+digit+"+\\.(?>"+digit+"*)?|\\."+digit+"+)");
    }
    @Override
    public Token newInstance(){
        return new FloatValueToken();
    }
    @Override
    public void load(){
        value = Double.parseDouble(text);
    }
    @Override
    public void run(Script script){
        script.push(new StackFloat(value));
    }
}