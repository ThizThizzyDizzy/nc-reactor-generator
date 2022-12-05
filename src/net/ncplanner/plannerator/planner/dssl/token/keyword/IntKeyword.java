package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
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
        script.push(new StackInt(0).cast(script.pop()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}