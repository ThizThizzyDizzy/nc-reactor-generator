package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackString;
public class InterpretKeyword extends Keyword{
    public InterpretKeyword(){
        super("interpret");
    }
    @Override
    public Keyword newInstance(){
        return new InterpretKeyword();
    }
    @Override
    public void run(Script script){
        StackString str = script.pop().asString();
        script.subscript(new Script(script.stack, script.variables, str, script.out));
    }
}