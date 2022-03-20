package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackMethod;
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
}