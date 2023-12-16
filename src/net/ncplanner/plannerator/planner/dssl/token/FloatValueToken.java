package net.ncplanner.plannerator.planner.dssl.token;
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
}