package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class ClearKeyword extends Keyword{
    public ClearKeyword(){
        super("clear");
    }
    @Override
    public Keyword newInstance(){
        return new ClearKeyword();
    }
    @Override
    public void run(Script script){
        script.pop().asCollection().clear();
    }
}