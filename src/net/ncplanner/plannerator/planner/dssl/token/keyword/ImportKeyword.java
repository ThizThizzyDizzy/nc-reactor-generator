package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ImportKeyword extends Keyword{
    public ImportKeyword(){
        super("import");
    }
    @Override
    public Keyword newInstance(){
        return new ImportKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}