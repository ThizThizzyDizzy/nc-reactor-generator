package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackCollection;
import net.ncplanner.plannerator.planner.dssl.object.StackMethod;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class ForeachKeyword extends Keyword{
    public ForeachKeyword(){
        super("foreach");
    }
    @Override
    public Keyword newInstance(){
        return new ForeachKeyword();
    }
    @Override
    public void run(Script script){
        StackMethod method = script.pop().asMethod();
        StackCollection collection = script.pop().asCollection();
        for(StackObject obj : (Iterable<StackObject>)collection.asCollection().collection()){
            script.foreachMarker();
            script.subscript(()->{script.push(obj);});
            script.subscript(method.getValue());
        }
        script.foreachEndMarker();
    }
}