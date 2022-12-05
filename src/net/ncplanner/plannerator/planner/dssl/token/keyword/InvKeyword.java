package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
public class InvKeyword extends Keyword{
    public InvKeyword(){
        super("inv");
    }
    @Override
    public Keyword newInstance(){
        return new InvKeyword();
    }
    @Override
    public void run(Script script){
//        script.push(new StackInt(-script.pop().asInt().getValue()-1)); // I dunno why you'd subtract one from it, but this is the commented out piece from https://github.com/tomdodd4598/Dodd-Simple-Stack-Language/blob/871550f8842057df96e0f078ef50588d82f6193c/src/dssl/interpret/element/value/primitive/IntElement.java#L49
        throw new UnsupportedOperationException("inv keyword is not supported!");//literally nothing supports it lol
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}