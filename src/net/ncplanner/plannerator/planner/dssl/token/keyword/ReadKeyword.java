package net.ncplanner.plannerator.planner.dssl.token.keyword;
import java.io.IOException;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
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