package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackInt;
import net.ncplanner.plannerator.planner.s_tack.object.StackObject;
import net.ncplanner.plannerator.planner.s_tack.token.Helpers;
public class RollKeyword extends Keyword{
    public RollKeyword(){
        super("roll");
    }
    @Override
    public Keyword newInstance(){
        return new RollKeyword();
    }
    @Override
    public void run(Script script){
        StackInt e1 = script.pop().asInt();
        StackInt e0 = script.pop().asInt();
        int count = e0.getValue().intValue();
        int roll = e1.getValue().intValue();
        if(count<0)throw new IllegalArgumentException("Count must not be negative!");
        StackObject[] objs = new StackObject[count];
        for(int i = 0; i<count; i++){
            objs[i] = script.pop();
        }
        for(int i = 0; i<count; i++){//I have no idea what this is actually doing; see https://github.com/tomdodd4598/Dodd-Simple-Stack-Language/blob/871550f8842057df96e0f078ef50588d82f6193c/src/dssl/interpret/Executor.java#L378
            script.push(objs[Helpers.mod(i-roll, count)]);
        }
    }
}