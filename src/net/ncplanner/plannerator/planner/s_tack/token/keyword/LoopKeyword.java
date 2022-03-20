package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackMethod;
public class LoopKeyword extends Keyword{
    public LoopKeyword(){
        super("loop");
    }
    @Override
    public Keyword newInstance(){
        return new LoopKeyword();
    }
    @Override
    public void run(Script script){
        StackMethod func = script.pop().asMethod();
        script.loopSubscript(func.getValue());
    }
}