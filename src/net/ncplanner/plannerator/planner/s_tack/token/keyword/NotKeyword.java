package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
public class NotKeyword extends Keyword{
    public NotKeyword(){
        super("not");
    }
    @Override
    public Keyword newInstance(){
        return new NotKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackBool(!script.pop().asBool().getValue()));
    }
}