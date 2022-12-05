package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class UnpackKeyword extends Keyword{
    public UnpackKeyword(){
        super("unpack");
    }
    @Override
    public Keyword newInstance(){
        return new UnpackKeyword();
    }
    @Override
    public void run(Script script){
        for(StackObject o : (Iterable<StackObject>)script.pop().asCollection().collection())script.push(o);
    }
    @Override
    public KeywordFlavor getFlavor() {
        return KeywordFlavor.COLLECTION;
    }
}