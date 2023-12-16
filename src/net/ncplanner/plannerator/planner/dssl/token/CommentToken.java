package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class CommentToken extends Token{
    public CommentToken(){
        super("(?>"+line_comment+"|"+block_comment+")", true);
    }
    @Override
    public Token newInstance(){
        return new CommentToken();
    }
}