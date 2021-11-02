package net.ncplanner.plannerator.planner.s_tack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
import net.ncplanner.plannerator.planner.s_tack.token.Token;
import simplelibrary.Queue;
import simplelibrary.Stack;
public class Script{
    public final Stack<StackObject> stack;
    public final HashMap<String, StackVariable> variables;
    public final ArrayList<Token> script;
    public int pos = 0;
    public Consumer<String> out;
    public Queue<Object> subscripts = new Queue<>();
    public Script(String script, Consumer<String> out){
        this(Tokenizer.tokenize(script), out);
    }
    public Script(ArrayList<Token> script, Consumer<String> out){
        this(new Stack<>(), new HashMap<>(), script, out);
        this.out = out;
    }
    public Script(Stack<StackObject> stack, HashMap<String, StackVariable> variables, String script, Consumer<String> out){
        this(stack, variables, Tokenizer.tokenize(script), out);
    }
    public Script(Stack<StackObject> stack, HashMap<String, StackVariable> variables, ArrayList<Token> script, Consumer<String> out){
        this.out = out;
        this.stack = stack;
        this.variables = variables;
        Tokenizer.cleanup(script);
        this.script = script;
    }
    public void run(){
        while(!isFinished())step();
    }
    public void step(){
        if(!subscripts.isEmpty()){
            Object subscript = subscripts.peek();
            if(subscript instanceof Runnable){
                ((Runnable)subscript).run();
                subscripts.dequeue();
                return;
            }
            Script s = (Script)subscript;
            s.step();
            if(s.isFinished()){
                s.pos = 0;
                subscripts.dequeue();
            }
            return;
        }
        if(isFinished())return;
        Token token = script.get(pos);
        try{
            token.run(this);
        }catch(Throwable t){
            print("--EXECUTION CRASHED--\n"+t.getClass().getName()+":\n"+t.getMessage());
            pos = script.size();
            for(StackTraceElement ste : t.getStackTrace())print(ste.toString());
            return;
        }
        pos++;
    }
    public void print(String s){
        out.accept(s);
    }
    public boolean isFinished(){
        return pos>=script.size()&&subscripts.isEmpty();
    }
    public void subscript(Runnable value){
        subscripts.enqueue(value);
    }
    public void subscript(Script value){
        subscripts.enqueue(new Script(stack, new HashMap<>(variables), value.script, out));
    }
    public Token getActive(){
        if(isFinished())return null;
        if(subscripts.isEmpty()){
            return script.get(pos);
        }else{
            Object o = subscripts.peek();
            if(o instanceof Script){
                return ((Script)o).getActive();
            }
            return  null;
        }
    }
}