package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackLabel;
import net.ncplanner.plannerator.planner.dssl.object.StackMacro;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class MacroKeyword extends Keyword{
    public MacroKeyword(){
        super("macro");
    }
    @Override
    public Keyword newInstance(){
        return new MacroKeyword();
    }
    @Override
    public void run(Script script){
        StackObject value = script.pop();
        StackLabel key = script.pop().asLabel();
        key.scope.variables.put(key.getValue(), new StackMacro(key.getValue(), value.getBaseObject().asMethod()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}