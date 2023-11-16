package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackLabel;
import net.ncplanner.plannerator.planner.dssl.object.StackMagic;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class MagicKeyword extends Keyword{
    public MagicKeyword(){
        super("magic");
    }
    @Override
    public Keyword newInstance(){
        return new MagicKeyword();
    }
    @Override
    public void run(Script script){
        StackObject value = script.pop();
        StackLabel key = script.pop().asLabel();
        key.scope.variables.put(Script.VAR_PREFIX_MAGIC+key.getValue(), new StackMagic(key.getValue(), value.getBaseObject().asMethod()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}