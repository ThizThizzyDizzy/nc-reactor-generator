package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackMethod;
public class RepeatKeyword extends Keyword{
    public RepeatKeyword(){
        super("repeat");
    }
    @Override
    public Keyword newInstance(){
        return new RepeatKeyword();
    }
    @Override
    public void run(Script script){
        StackMethod func = script.pop().asMethod();
        int repeats = (int)(long)script.pop().asInt().getValue();
        script.repeatSubscript(func.getValue(), repeats);
    }
}