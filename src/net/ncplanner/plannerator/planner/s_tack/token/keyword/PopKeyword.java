package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class PopKeyword extends Keyword{
    public PopKeyword(){
        super("pop");
    }
    @Override
    public Keyword newInstance(){
        return new PopKeyword();
    }
    @Override
    public void run(Script script){
        script.pop();
    }
}