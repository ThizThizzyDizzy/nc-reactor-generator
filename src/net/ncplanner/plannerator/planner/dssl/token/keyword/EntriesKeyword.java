package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackList;
public class EntriesKeyword extends Keyword{
    public EntriesKeyword(){
        super("entries");
    }
    @Override
    public Keyword newInstance(){
        return new EntriesKeyword();
    }
    @Override
    public void run(Script script){
        script.push(new StackList(script.pop().asDict().toList()));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.COLLECTION;
    }
}