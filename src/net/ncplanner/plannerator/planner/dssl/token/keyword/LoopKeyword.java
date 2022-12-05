package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackMethod;
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
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}