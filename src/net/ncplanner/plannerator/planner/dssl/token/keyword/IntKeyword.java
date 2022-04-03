package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
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