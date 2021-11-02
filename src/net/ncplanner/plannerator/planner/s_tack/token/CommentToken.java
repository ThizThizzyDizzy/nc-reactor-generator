package net.ncplanner.plannerator.planner.s_tack.token;
import net.ncplanner.plannerator.planner.s_tack.Script;
import static net.ncplanner.plannerator.planner.s_tack.token.Helpers.*;
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