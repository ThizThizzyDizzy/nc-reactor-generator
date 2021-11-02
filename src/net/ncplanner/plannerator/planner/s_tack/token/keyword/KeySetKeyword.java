package net.ncplanner.plannerator.planner.s_tack.token.keyword;
public class KeySetKeyword extends Keyword{
    public KeySetKeyword(){
        super("keyset");
    }
    @Override
    public Keyword newInstance(){
        return new KeySetKeyword();
    }
}