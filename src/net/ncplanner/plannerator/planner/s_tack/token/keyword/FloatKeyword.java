package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
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