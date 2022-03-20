package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
public class StringKeyword extends Keyword{
    public StringKeyword(){
        super("string");
    }
    @Override
    public Keyword newInstance(){
        return new StringKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asString());
    }
}