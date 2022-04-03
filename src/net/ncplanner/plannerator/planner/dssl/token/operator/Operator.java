package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.token.Token;
public abstract class Operator extends Token{
    public Operator(String operator){
        super(escapify(operator));
    }
    @Override
    public abstract Operator newInstance();
    private static String escapify(String operator){
        String str = "";
        for(char c : operator.toCharArray()){
            str+="\\"+c;
        }
        return str;
    }
    @Override
    public final void load(){}
    @Override
    public final void run(Script script){
        StackObject v2 = script.pop();
        StackObject v1 = script.pop();
        StackObject ret = evaluate(script, v1, v2);
        if(ret!=null)script.push(ret);
    }
    public abstract StackObject evaluate(Script script, StackObject v1, StackObject v2);
}