package net.ncplanner.plannerator.planner.dssl.token.operator;
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
    public String getOverload(){
        return null;
    }
}
