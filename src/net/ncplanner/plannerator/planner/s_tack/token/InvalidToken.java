package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import static net.ncplanner.plannerator.planner.s_tack.token.Helpers.*;
public class InvalidToken extends Token{
    public InvalidToken(){
        super("["+all.substring(1, all.length()-1)+"&&[^"+separators.substring(1, separators.length()-1)+"]]+", true);
    }
    @Override
    public Token newInstance(){
        return new InvalidToken();
    }
    @Override
    public void run(Script script){
        throw new RuntimeException("Invalid token: "+text);
    }
}