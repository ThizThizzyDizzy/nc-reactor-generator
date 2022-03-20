package net.ncplanner.plannerator.planner.s_tack;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.s_tack.object.StackFlow;
import net.ncplanner.plannerator.planner.s_tack.object.StackNull;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackString;
import net.ncplanner.plannerator.planner.s_tack.object.StackVariable;
import net.ncplanner.plannerator.planner.s_tack.token.Token;
public class Script{
    public final Stack<StackObject> stack;
    public final HashMap<String, StackVariable> variables;
    public final ArrayList<Token> script;
    public int pos = 0;
    public Consumer<String> out;
    public Queue<Object> subscripts = new Queue<>();
    public BufferedReader in;
    private long repeating;
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
    public Script(Stack<StackObject> stack, HashMap<String, StackVariable> variables, StackString str, Consumer<String> out){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void run(Collection<Token> breakpoints){
        while(!isFinished()){
            step();
            if(breakpoints!=null){
                Script s = this;
                while(!s.subscripts.isEmpty()&&(s.subscripts.peek() instanceof Script))s = (Script)s.subscripts.peek();
                if(s.script.size()>s.pos){
                    Token token = s.script.get(s.pos);
                    if(breakpoints.contains(token))break;//stop running!
                }
            }
        }
    }
    public void step(){
        Object subscript = subscripts.peek();
        while(subscript instanceof Integer&&(int)subscript<-1){//clear foreach markers
            subscripts.dequeue();
            subscript = subscripts.peek();
        }
        if(!subscripts.isEmpty()){
            if(subscript instanceof Integer){
                repeating = (int)subscripts.dequeue();
                subscript = subscripts.peek();
            }
            if(subscript instanceof Runnable){
                ((Runnable)subscript).run();
                subscripts.dequeue();
                return;
            }
            Script s = (Script)subscript;
            s.step();
            if(repeating!=0){
                if(peekOrNull().getType()==StackObject.Type.FLOW){
                    switch(pop().asFlow().type){
                        case BREAK:
                            repeating = 0;
                        case CONTINUE:
                            s.pos = s.script.size();
                            break;
                    }
                }
            }else if(peekOrNull().getType()==StackObject.Type.FLOW){
                switch(pop().asFlow().type){
                    case BREAK:
                        long lookingFor = -3;
                        boolean found = false;
                        while(!subscripts.isEmpty()){
                            if(subscripts.dequeue().equals(lookingFor)){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            pos = script.size();
                            push(new StackFlow(StackFlow.Flow.BREAK));
                        }
                        break;
                    case CONTINUE:
                        lookingFor = -2;
                        found = false;
                        while(!subscripts.isEmpty()){
                            if(subscripts.dequeue().equals(lookingFor)){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            pos = script.size();
                            push(new StackFlow(StackFlow.Flow.CONTINUE));
                        }
                        break;
                }
                return;
            }
            if(s.isFinished()){
                s.pos = 0;
                if(repeating>0)repeating--;
                else if(repeating==0)subscripts.dequeue();
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
        Script that = this;
        subscripts.enqueue(new Script(stack, new HashMap<>(variables), value.script, out){
            @Override
            public void halt(){
                that.halt();
            }
        });
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
    public StackObject peekOrNull(){
        return stack.isEmpty()?StackNull.INSTANCE:stack.peek();
    }
    public StackObject peek(){
        return stack.peek();
    }
    public StackObject[] peek(int count){
        StackObject[] peeked = new StackObject[count];
        int i = 0;
        for(Iterator<StackObject> it = stack.iterator(); it.hasNext();){
            peeked[i] = it.next();
            i++;
            if(i==count)break;
        }
        return peeked;
    }
    public StackObject peekAt(int depth){
        int i = 0;
        for(Iterator<StackObject> it = stack.iterator(); it.hasNext();){
            if(i==depth)return it.next();
            else it.next();
            i++;
        }
        throw new IndexOutOfBoundsException("Peeking outside the stack! "+depth+" is too far!");
    }
    public StackObject pop(){
        return stack.pop();
    }
    public void push(StackObject obj){
        stack.push(obj);
    }
    public void halt(){
        subscripts.clear();
        pos = script.size();
        repeating = 0;
    }
    public void loopSubscript(Script script){
        repeatSubscript(script, -1);
    }
    public void repeatSubscript(Script script, int repeats){
        subscripts.enqueue(repeats);
        subscript(script);
    }
    public void foreachMarker(){
        subscripts.enqueue(-2);
    }
    public void foreachEndMarker(){
        subscripts.enqueue(-3);
    }
}