package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
public class InterpretKeyword extends Keyword{
    public InterpretKeyword(){
        super("interpret");
    }
    @Override
    public Keyword newInstance(){
        return new InterpretKeyword();
    }
    @Override
    public void run(Script script){
        StackString str = script.pop().asString();
        script.subscript(new Script(script.stack, script.variables, str, script.out));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}