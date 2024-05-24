package net.ncplanner.plannerator.planner.dssl;
import dssl.NativeImpl;
import dssl.interpret.BuiltIn;
import dssl.interpret.ClazzType;
import dssl.interpret.Hooks;
import dssl.interpret.Interpreter;
import dssl.interpret.TokenExecutor;
import dssl.interpret.TokenIterator;
import dssl.interpret.TokenResult;
import dssl.interpret.element.BlockElement;
import dssl.interpret.element.Element;
import dssl.interpret.element.LabelElement;
import dssl.interpret.element.ModuleElement;
import dssl.interpret.element.primitive.StringElement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.dssl.token.BlankToken;
import net.ncplanner.plannerator.planner.dssl.token.Token;
public class DSSLInterpreter{
    public Interpreter interpreter;
    public int runSteps = 0;
    private boolean running = false;
    private boolean finished;
    private boolean haltExecution;
    public HashSet<Integer> breakpoints;
    private int lastLine;
    public Token currentToken;
    public DSSLInterpreter(File file, Consumer<String> print, Consumer<String> debug, Supplier<String> read) throws IOException{
        DSSLLexerIterator iter = new DSSLLexerIterator(file, this::canContinue);
        interpreter = new Interpreter(Arrays.asList(file.getAbsolutePath()), new Hooks() {
            @Override
            public void print(String str){
                print.accept(str);
            }
            @Override
            public void debug(String str){
                debug.accept(str);
            }
            @Override
            public String read(){
                return read.get();
            }
            @Override
            public TokenResult onInclude(TokenExecutor exec){
                Element elem = exec.pop();
                StringElement stringElem = elem.asString(exec);
                if(stringElem != null){
                    try{
                        return new TokenExecutor(new DSSLLexerIterator(new File(file.getAbsoluteFile().getParentFile(), stringElem.toString()), DSSLInterpreter.this::canContinue), exec, false).iterate();
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }else if(elem instanceof ModuleElement){
                    exec.putAll(((ModuleElement) elem).internal, true, true);
                    return TokenResult.PASS;
                }else throw new IllegalArgumentException(String.format("Keyword \"include\" requires %s or %s element as argument!", BuiltIn.STRING, BuiltIn.MODULE));
            }
            @Override
            public TokenResult onImport(TokenExecutor exec){
                Element elem1 = exec.pop(), elem0 = exec.pop();
                if(!(elem0 instanceof LabelElement)){
                    throw new IllegalArgumentException(String.format("Keyword \"import\" requires %s element as first argument!", BuiltIn.LABEL));
                }

                LabelElement label = (LabelElement) elem0;
                StringElement stringElem = elem1.asString(exec);
                if(stringElem != null){
                    try{
                        TokenExecutor otherExec = exec.interpreter.newExecutor(new DSSLLexerIterator(new File(file.getAbsoluteFile().getParentFile(), stringElem.toString()), DSSLInterpreter.this::canContinue));
                        label.setClazz(ClazzType.INTERNAL, otherExec, new ArrayList<>());
                        return otherExec.iterate();
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }else if(elem1 instanceof ModuleElement){
                    label.setClazz(ClazzType.INTERNAL, ((ModuleElement) elem1).internal, new ArrayList<>());
                    return TokenResult.PASS;
                }else throw new IllegalArgumentException(String.format("Keyword \"import\" requires %s or %s element as second argument!", BuiltIn.STRING, BuiltIn.MODULE));
            }
            @Override
            public TokenResult onNative(TokenExecutor exec){
                return NativeImpl.INSTANCE.onNative(exec);
            }
            @Override
            public TokenIterator getBlockIterator(TokenExecutor exec, BlockElement block){
                return iter.getBlockIterator(exec, block);
            }
            @Override
            public Path getRootPath(TokenExecutor exec){
                return file.toPath();
            }
        }, iter, false);
    }
    private boolean canContinue(Token token, int line){
        if(token instanceof BlankToken)return true;//skip whitespace
        currentToken = token;
        if(haltExecution)throw new RuntimeException("Execution Halted");
        if(runSteps==0)return false;//paused
        if(runSteps>0){//step was hit, ignore breakpoints
            runSteps--;
            return true;
        }else{//continuous running, pay attention to breakpoints
            if(lastLine!=line){
                lastLine = line;
                if(breakpoints!=null&&breakpoints.contains(line)){
                    runSteps = 0;//hit breakpoint, pause
                    return false;
                }
            }
            return true;
        }
    }
    public void start(int runSteps){
        this.runSteps = runSteps;
        if(!running){
            running = true;
            RuntimeException e = null;
            try{
                interpreter.run();
            }catch(RuntimeException ex){
                e = ex;
            }
            finished = true;
            if(e!=null)throw e;
        }
    }
    public void run(){
        start(-1);
    }
    public void step(){
        start(1);
    }
    public void halt(){
        haltExecution = true;
    }
    boolean isFinished(){
        return finished;
    }
}