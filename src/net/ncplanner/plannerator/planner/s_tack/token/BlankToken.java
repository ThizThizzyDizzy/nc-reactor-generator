package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import static net.ncplanner.plannerator.planner.s_tack.token.Helpers.*;
public class BlankToken extends Token{
    public BlankToken(){
        super(separator+"+", true);
    }
    @Override
    public Token newInstance(){
        return new BlankToken();
    }
    @Override
    public void run(Script script){}
}