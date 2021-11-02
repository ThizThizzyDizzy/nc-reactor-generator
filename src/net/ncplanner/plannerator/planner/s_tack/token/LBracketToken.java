package net.ncplanner.plannerator.planner.s_tack.token;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackCollection;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
public class LBracketToken extends Token{
    public LBracketToken(){
        super("\\[");
    }
    @Override
    public Token newInstance(){
        return new LBracketToken();
    }
    @Override
    public void run(Script script){
        ArrayList<StackObject> collection = new ArrayList<>();
        while(true){
            script.pos++;
            Token token = script.script.get(script.pos);
            if(token instanceof RBracketToken){
                break;
            }
            token.run(script);
            collection.add(script.stack.pop());
        }
        script.stack.push(new StackCollection(collection));
    }
}