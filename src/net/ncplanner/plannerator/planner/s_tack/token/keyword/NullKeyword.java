package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackNull;
public class NullKeyword extends Keyword{
    public NullKeyword(){
        super("null");
    }
    @Override
    public Keyword newInstance(){
        return new NullKeyword();
    }
    @Override
    public void run(Script script){
        script.push(StackNull.INSTANCE);
    }
}