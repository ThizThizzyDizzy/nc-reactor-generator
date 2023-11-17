package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackClassInstance;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackType;
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
        StackType type = script.pop().asType();
        StackObject toCast = script.pop();
        if(toCast.getBaseType()==StackObject.Type.CLASS_INSTANCE){
            script.push(toCast.getBaseObject());
            script.subscript(((StackClassInstance)toCast.getBaseObject()).castToString());
            return;
        }
        script.push(type.internal.cast(toCast));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.TYPE;
    }
}