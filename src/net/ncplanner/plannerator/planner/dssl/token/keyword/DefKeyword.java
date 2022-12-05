package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackLabel;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class DefKeyword extends Keyword{
    public DefKeyword(){
        super("def");
    }
    @Override
    public Keyword newInstance(){
        return new DefKeyword();
    }
    @Override
    public void run(Script script){
        StackObject value = script.pop();
        StackLabel key = script.pop().asLabel();
        script.variables.put(key.getValue(), new StackVariable(key.getValue(), value.getBaseObject()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}