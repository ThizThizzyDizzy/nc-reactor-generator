package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import java.util.ArrayList;
import java.util.Stack;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackLBracket;
import net.ncplanner.plannerator.planner.s_tack.object.StackList;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.object.StackRBracket;
public class ListKeyword extends Keyword{
    public ListKeyword(){
        super("list");
    }
    @Override
    public Keyword newInstance(){
        return new ListKeyword();
    }
    @Override
    public void run(Script script){
        StackObject elem = script.pop();
        if(elem instanceof StackRBracket){
            Stack<StackObject> elems = new Stack<>();
            while(!((elem = script.pop()) instanceof StackLBracket)){
                elems.push(elem);
            }
            ArrayList<StackObject> flipped = new ArrayList<>();
            while(!elems.isEmpty())flipped.add(elems.pop());
            script.push(new StackList(flipped));
        }else script.push(elem.asList());
    }
}