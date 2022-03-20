package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class IntKeyword extends Keyword{
    public IntKeyword(){
        super("int");
    }
    @Override
    public Keyword newInstance(){
        return new IntKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asInt());
    }
}