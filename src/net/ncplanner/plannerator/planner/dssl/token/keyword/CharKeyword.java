package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class CharKeyword extends Keyword{
    public CharKeyword(){
        super("char");
    }
    @Override
    public Keyword newInstance(){
        return new CharKeyword();
    }
    @Override
    public void run(Script script){
        script.push(script.pop().asChar());
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}