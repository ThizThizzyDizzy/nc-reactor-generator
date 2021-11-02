package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
public class SizeKeyword extends Keyword{
    public SizeKeyword(){
        super("size");
    }
    @Override
    public Keyword newInstance(){
        return new SizeKeyword();
    }
    @Override
    public void run(Script script){
        script.stack.push(new StackInt(script.stack.pop().asCollection().getValue().length));
    }
}