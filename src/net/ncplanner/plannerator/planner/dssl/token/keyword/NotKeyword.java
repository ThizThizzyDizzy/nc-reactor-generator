package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackBool;
public class NotKeyword extends Keyword{
    public NotKeyword(){
        super("not");
    }
    @Override
    public Keyword newInstance(){
        return new NotKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackBool(!script.pop().asBool().getValue()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}