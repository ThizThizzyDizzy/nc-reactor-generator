package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class BoolKeyword extends Keyword{
    public BoolKeyword(){
        super("bool");
    }
    @Override
    public Keyword newInstance(){
        return new BoolKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asBool());
    }
}