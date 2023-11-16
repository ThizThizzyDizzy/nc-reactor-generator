package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
import net.ncplanner.plannerator.planner.dssl.object.StackType;
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
        script.push(new StackType(new StackString(null)));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}