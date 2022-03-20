package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class DupKeyword extends Keyword{
    public DupKeyword(){
        super("dup");
    }
    @Override
    public Keyword newInstance(){
        return new DupKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.peek());
    }
}