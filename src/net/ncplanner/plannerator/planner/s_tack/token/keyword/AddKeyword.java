package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class AddKeyword extends Keyword{
    public AddKeyword(){
        super("add");
    }
    @Override
    public Keyword newInstance(){
        return new AddKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(new StackBool(script.pop().asCollection().add(elem)));
    }
}