package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class CommentToken extends Token{
    public CommentToken(){
        super("(?>"+line_comment+"|"+block_comment+")", true);
    }
    @Override
    public Token newInstance(){
        return new CommentToken();
    }
    @Override
    public void run(Script script){}
}