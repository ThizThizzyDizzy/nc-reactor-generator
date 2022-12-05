package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackMethod;
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
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}