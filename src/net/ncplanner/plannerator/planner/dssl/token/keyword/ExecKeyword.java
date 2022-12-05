package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackMethod;
public class ExecKeyword extends Keyword{
    public ExecKeyword(){
        super("exec");
    }
    @Override
    public Keyword newInstance(){
        return new ExecKeyword();
    }
    @Override
    public void run(Script script){
        StackMethod method = script.pop().asMethod();
        script.subscript(method.getValue());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}