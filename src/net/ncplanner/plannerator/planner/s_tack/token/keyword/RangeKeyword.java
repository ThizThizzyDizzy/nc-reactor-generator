package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackCollection;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class RangeKeyword extends Keyword{
    public RangeKeyword(){
        super("range");
    }
    @Override
    public Keyword newInstance(){
        return new RangeKeyword();
    }
    @Override
    public void run(Script script){
        StackObject obj = script.stack.pop();
        int start,stop,step;
        if(obj.getBaseType()==StackObject.Type.COLLECTION){
            StackObject[] args = obj.asCollection().getValue();
            if(args.length<1||args.length>3)throw new IllegalArgumentException("Wrong number of arguments! expected 1-3, found "+args.length);
            for(StackObject o : args){
                if(o.getBaseType()!=StackObject.Type.INT)throw new IllegalArgumentException("Invalid argument type! expected int, found "+o.getType().toString());
            }
            start = args.length>1?args[0].asInt().getValue():0;
            stop = (args.length>1?args[1]:args[0]).asInt().getValue();
            step = args.length==3?args[2].asInt().getValue():1;
        }else if(obj.getBaseType()==StackObject.Type.INT){
            start = 0;
            stop = obj.asInt().getValue();
            step = 1;
        }else throw new IllegalArgumentException("Invalid range argument! expected int or collection, found "+obj.getBaseType().toString());
        if(Math.signum(step)!=Math.signum(stop-start)){
            throw new IllegalArgumentException("Step must match range direction!");
        }
        if(stop==start){
            throw new IllegalArgumentException("range is zero!");
        }
        ArrayList<StackObject> output = new ArrayList<>();
        for(int i = start; stop>start?i<stop:(i>start); i+=step){
            output.add(new StackInt(i));
        }
        script.stack.push(new StackCollection(output));
    }
}