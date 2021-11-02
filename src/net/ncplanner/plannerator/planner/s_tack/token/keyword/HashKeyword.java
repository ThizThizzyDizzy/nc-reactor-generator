package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class HashKeyword extends Keyword{
    public HashKeyword(){
        super("hash");
    }
    @Override
    public Keyword newInstance(){
        return new HashKeyword();
    }
}