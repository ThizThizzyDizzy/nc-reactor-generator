package net.ncplanner.plannerator.planner.dssl.token;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackMethod;
public class LBraceToken extends Token{
    public LBraceToken(){
        super("\\{");
    }
    @Override
    public Token newInstance(){
        return new LBraceToken();
    }
    @Override
    public void run(Script script){
        ArrayList<Token> subscript = new ArrayList<>();
        //current position is this token!
        int depth = 0;
        while(true){
            script.pos++;
            Token token = script.script.get(script.pos);
            if(token instanceof LBraceToken){
                depth++;
            }
            if(token instanceof RBraceToken){
                depth--;
                if(depth<0)break;
            }
            subscript.add(token);
        }
        script.push(new StackMethod(new Script(script.stack, script.variables, subscript, script.out)));
    }
}