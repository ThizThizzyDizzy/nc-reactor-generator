package net.ncplanner.plannerator.planner.s_tack.token.keyword;
import java.io.IOException;
import net.ncplanner.plannerator.planner.s_tack.Script;
import net.ncplanner.plannerator.planner.s_tack.object.StackString;
public class ReadKeyword extends Keyword{
    public ReadKeyword(){
        super("read");
    }
    @Override
    public Keyword newInstance(){
        return new ReadKeyword();
    }
    @Override
    public void run(Script script){
        try{
            script.push(new StackString(script.in.readLine()));
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}