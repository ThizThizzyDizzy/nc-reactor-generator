package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackList;
public class EntriesKeyword extends Keyword{
    public EntriesKeyword(){
        super("entries");
    }
    @Override
    public Keyword newInstance(){
        return new EntriesKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackList(script.pop().asDict().toList()));
    }
}