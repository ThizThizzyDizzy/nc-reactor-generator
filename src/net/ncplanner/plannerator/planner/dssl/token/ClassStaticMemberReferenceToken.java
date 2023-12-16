package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class ClassStaticMemberReferenceToken extends Token{
    public String identifier;
    public ClassStaticMemberReferenceToken(){
        super("::"+name);
    }
    @Override
    public Token newInstance(){
        return new ClassStaticMemberReferenceToken();
    }
    @Override
    public void load(){
        identifier = text.substring(2);
    }
}