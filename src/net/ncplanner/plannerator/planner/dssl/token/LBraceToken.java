package net.ncplanner.plannerator.planner.dssl.token;
import java.util.ArrayList;
public class LBraceToken extends Token{
    public LBraceToken(){
        super("\\{");
    }
    @Override
    public Token newInstance(){
        return new LBraceToken();
    }
}