package net.ncplanner.plannerator.planner.dssl.object;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class StackRange extends StackCollection<List<StackObject>>{
    long start, stop, step = 1;
    private static long[] staticAbuseArgs = new long[3];
    public StackRange(Collection<StackObject> elems){
        this(new ArrayList<>(elems));
    }
    public StackRange(List<StackObject> elems){
        super(parse(elems, staticAbuseArgs));
        start = staticAbuseArgs[0];
        stop = staticAbuseArgs[1];
        step = staticAbuseArgs[2];
    }
    public StackRange(StackObject[] elems){
        super(parse(elems, staticAbuseArgs));
        start = staticAbuseArgs[0];
        stop = staticAbuseArgs[1];
        step = staticAbuseArgs[2];
    }
    public StackRange(long start, long stop, long step){
        super(range(start, stop, step));
        this.start = start;
        this.stop = stop;
        this.step = step;
    }
    private static List<StackObject> parse(StackObject[] elems, long[] theArgs){
        long[] args = new long[elems.length];
        if(args.length<1||args.length>3)throw new IllegalArgumentException("Wrong number of arguments! expected 1-3, found "+args.length);
        for(int i = 0; i<elems.length; i++){
            args[i] = elems[i].asInt().getValue();
        }
        long start = 0, stop = 0, step = 1;
        if(args.length==1)stop = args[0];
        if(args.length>1){
            start = args[0];
            stop = args[1];
            if(args.length==3)step = args[2];
        }
        if(step==0)throw new IllegalArgumentException("Step must not be zero!");
        if(Math.signum(step)!=Math.signum(stop-start))throw new IllegalArgumentException("Step must match range direction!");
        theArgs[0] = start;
        theArgs[1] = stop;
        theArgs[2] = step;
        return range(start, stop, step);
    }
    private static List<StackObject> parse(List<StackObject> elems, long[] theArgs){
        long[] args = new long[elems.size()];
        if(args.length<1||args.length>3)throw new IllegalArgumentException("Wrong number of arguments! expected 1-3, found "+args.length);
        for(int i = 0; i<elems.size(); i++){
            args[i] = elems.get(i).asInt().getValue();
        }
        long start = 0, stop = 0, step = 1;
        if(args.length==1)stop = args[0];
        if(args.length>1){
            start = args[0];
            stop = args[1];
            if(args.length==3)step = args[2];
        }
        if(step==0)throw new IllegalArgumentException("Step must not be zero!");
        if(Math.signum(step)!=Math.signum(stop-start))throw new IllegalArgumentException("Step must match range direction!");
        theArgs[0] = start;
        theArgs[1] = stop;
        theArgs[2] = step;
        return range(start, stop, step);
    }
    private static List<StackObject> range(long start, long stop, long step){
        ArrayList<StackObject> output = new ArrayList<>();
        for(long i = start; stop>start?i<stop:(i>stop); i+=step){
            output.add(new StackInt(i));
        }
        return output;
    }
    @Override
    public String getTypeString(){
        return "range";
    }
    @Override
    public String toString(){
        return "range["+start+","+stop+","+step+"]";
    }
    @Override
    public StackObject duplicate(){
        return new StackRange(start, stop, step); 
    }
    @Override
    public StackList asList(){
        return new StackList(value);
    }
    @Override
    public StackTuple asTuple(){
        return new StackTuple(value);
    }
    @Override
    public StackSet asSet(){
        return new StackSet(value);
    }
    @Override
    public Collection<StackObject> collection(){
        return value;
    }
    @Override
    public long size(){
        return value.size();
    }
    @Override
    public boolean isEmpty(){
        return value.isEmpty();
    }
    @Override
    public StackObject cast(StackObject obj){
        return obj.asRange();
    }
    @Override
    public StackObject get(StackObject elem){
        return value.get((int)(long)elem.asInt().getValue());
    }
    @Override
    public boolean containsAll(Collection<StackObject> collection){
        return value.containsAll(collection);
    }
    @Override
    public boolean contains(StackObject elem){
        return value.contains(elem);
    }
}