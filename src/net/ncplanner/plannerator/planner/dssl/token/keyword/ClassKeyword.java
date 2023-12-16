package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ClassKeyword extends Keyword{
    public ClassKeyword(){
        super("class");
    }
    @Override
    public Keyword newInstance(){
        return new ClassKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}