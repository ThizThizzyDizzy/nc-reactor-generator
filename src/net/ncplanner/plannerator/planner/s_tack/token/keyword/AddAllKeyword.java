package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackBool;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class AddAllKeyword extends Keyword{
    public AddAllKeyword(){
        super("addall");
    }
    @Override
    public Keyword newInstance(){
        return new AddAllKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(new StackBool(script.pop().asCollection().addAll(elem.asCollection().collection())));
    }
}