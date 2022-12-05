package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class CastKeyword extends Keyword{
    public CastKeyword(){
        super("cast");
    }
    @Override
    public Keyword newInstance(){
        return new CastKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        script.push(elem.asType().internal.cast(script.pop()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}