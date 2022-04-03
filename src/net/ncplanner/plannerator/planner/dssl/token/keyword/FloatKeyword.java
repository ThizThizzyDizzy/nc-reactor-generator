package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class FloatKeyword extends Keyword{
    public FloatKeyword(){
        super("float");
    }
    @Override
    public Keyword newInstance(){
        return new FloatKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asFloat());
    }
}