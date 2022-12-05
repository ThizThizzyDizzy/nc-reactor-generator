package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
import net.ncplanner.plannerator.planner.dssl.object.StackMethod;
public class IfKeyword extends Keyword{
    public IfKeyword(){
        super("if");
    }
    @Override
    public Keyword newInstance(){
        return new IfKeyword();
    }
    @Override
    public void run(Script script){
        StackMethod method = script.pop().asMethod();
        StackBool condition = script.pop().asBool();
        if(condition.getValue())script.subscript(method.getValue());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.FLOW;
    }
}