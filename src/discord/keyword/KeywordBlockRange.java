package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordBlockRange extends Keyword{
    public String fuel;
    public KeywordBlockRange(){
        super("Block Range");
    }
    @Override
    public boolean doRead(String input){
        this.fuel = input;
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(255, 0, 127);
    }
    @Override
    public String getRegex(){
        return "((([fF][eE][wW][eE][rR]|[mM][oO][rR][eE]|[lL][eE][sS]{2}|[gG][rR][eE][aA][tT][eE][rR]) ?[tT][hH][eEaA][nN]|[eE][xX][aA][cC][tT][lL][yY]) ?)?([nN][oO] |[<>]?\\d+( ?([tT][oO]|-) ?\\d+)?) ?(([lL][iI][qQ][uU][iI][dD]|[fF][uU][eE][lL]|[hH][eE][aA][vV][yY]) ?)?[a-zA-Z-]{4,}( ?([cC][oO][oO][lL][eE][rR][sS]?|([hH][eE][aA][tT])?([sS][iI][nN][kK]|[eE][rR])[sS]?|[mM][oO][dD][eE][rR][aA][tT][oO][rR][sS]?|[rR][eE][fF][lL][eE][cC][tT][oO][rR][sS]?|[sS][hH][iI][eE][lL][dD][sS]?))?";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordBlockRange();
    }
}