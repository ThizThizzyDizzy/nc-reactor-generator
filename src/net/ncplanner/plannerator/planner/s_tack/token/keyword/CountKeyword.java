package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
public class CountKeyword extends Keyword{
    public CountKeyword(){
        super("count");
    }
    @Override
    public Keyword newInstance(){
        return new CountKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackInt(script.stack.size()));
    }
}