package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import java.util.Iterator;
import java.util.Stack;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackLabel;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class CountToKeyword extends Keyword{
    public CountToKeyword(){
        super("countto");
    }
    @Override
    public Keyword newInstance(){
        return new CountToKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        boolean includeLabel = false;
        if(elem.getType()!=StackObject.Type.LABEL){
            boolean valid = false;
            if(elem.getType()==StackObject.Type.BOOL){
                StackLabel label = script.pop().asLabel();
                includeLabel = elem.asBool().getValue();
                elem = label;
                valid = true;
            }
            if(!valid)throw new IllegalArgumentException("invalid args for countto! requires label and optional bool!");
        }
        script.push(new StackInt(countElemsToLabel(script.stack, elem.asLabel().getValue(), includeLabel)));
    }
    private long countElemsToLabel(Stack<StackObject> stack, String value, boolean includeLabel){
        int count = 0;
        Iterator<StackObject> it = stack.iterator();
        while(it.hasNext()){
            StackObject elem = it.next();
            if(elem.getType()==StackObject.Type.LABEL&&elem.asLabel().getValue().equals(value)){
                return includeLabel?count+1:count;
            }else count++;
        }
        throw new IllegalArgumentException("Label "+value+" does not exist on stack!");
    }
}