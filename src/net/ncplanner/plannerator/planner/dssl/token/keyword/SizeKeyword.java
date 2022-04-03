package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class SizeKeyword extends Keyword{
    public SizeKeyword(){
        super("size");
    }
    @Override
    public Keyword newInstance(){
        return new SizeKeyword();
    }
    @Override
    public void run(Script script){
        StackObject obj = script.pop();
        switch(obj.getType()){
            case BOOL:
            case CHAR:
            case FLOAT:
            case INT:
            case STRING:
                script.push(new StackInt(1));
                return;
        }
        script.push(new StackInt(obj.asCollection().size()));
    }
}