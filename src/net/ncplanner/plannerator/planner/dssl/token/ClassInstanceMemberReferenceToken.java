package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class ClassInstanceMemberReferenceToken extends Token{
    public String identifier;
    public ClassInstanceMemberReferenceToken(){
        super("."+name);
    }
    @Override
    public Token newInstance(){
        return new ClassInstanceMemberReferenceToken();
    }
    @Override
    public void load(){
        identifier = text.substring(1);
    }
}