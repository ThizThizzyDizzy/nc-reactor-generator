package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class PutKeyword extends Keyword{
    public PutKeyword(){
        super("put");
    }
    @Override
    public Keyword newInstance(){
        return new PutKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem2 = script.pop();
        StackObject elem1 = script.pop();
        script.push(script.pop().asCollection().put(elem1, elem2));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}