package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackMethod;
public class IfElseKeyword extends Keyword{
    public IfElseKeyword(){
        super("ifelse");
    }
    @Override
    public Keyword newInstance(){
        return new IfElseKeyword();
    }
    @Override
    public void run(Script script){
        StackMethod elseMethod = script.pop().asMethod();
        StackMethod method = script.pop().asMethod();
        StackBool condition = script.pop().asBool();
        if(condition.getValue())script.subscript(method.getValue());
        else script.subscript(elseMethod.getValue());
    }
}