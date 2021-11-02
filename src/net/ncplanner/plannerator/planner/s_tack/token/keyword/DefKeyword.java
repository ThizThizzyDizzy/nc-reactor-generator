package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackLabel;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
public class DefKeyword extends Keyword{
    public DefKeyword(){
        super("def");
    }
    @Override
    public Keyword newInstance(){
        return new DefKeyword();
    }
    @Override
    public void run(Script script){
        StackObject value = script.stack.pop();
        StackLabel key = script.stack.pop().asLabel();
        script.variables.put(key.getValue(), new StackVariable(key.getValue(), value.getBaseObject()));
    }
}