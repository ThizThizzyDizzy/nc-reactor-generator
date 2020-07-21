package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordMultiblock extends Keyword{
    public KeywordMultiblock(){
        super("Multiblock");
    }
    @Override
    public boolean doRead(String input){
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(0, 255, 0);
    }
    @Override
    public String getRegex(){
        return "([sS][fF][rR]|[mM][sS][rR]|[sS][oO][lL][iI][dD][ -]?[fF][uu][eE][lL][eE][dD]|[mM][oO][lL][tT][eE][nN][ -]?[sS][aA][lL][tT])( ?[rR][eE][aA][cC][tT][oO][rR])?|([rR][eE][aA][cC][tT][oO][rR])";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordMultiblock();
    }
}