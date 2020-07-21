package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordFuel extends Keyword{
    public String fuel;
    public KeywordFuel(){
        super("Fuel");
    }
    @Override
    public boolean doRead(String input){
        this.fuel = input;
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(255, 255, 0);
    }
    @Override
    public String getRegex(){
        return "([a-zA-Z]+[ -]?\\d{3}\\[[a-zA-Z0-9]+\\]|\\[[a-zA-Z0-9]+\\][a-zA-Z]+[ -]?\\d{3}|([a-zA-Z]+[ -]?\\d{3}|[tT][bB][uU])([ -]?([oO][xX]([iI][dD][eE])?|[nN][iI]([tT][rR][iI][dD][eE])?|([tT][eE][tT][rR][aA])?[fF](4|[lL][uU][oO][rR][iI][dD][eE])|[zZ]([aA]|[iI][rR][cC][oO][nN][iI][uU][mM]([ -]?[aA][lL]{2}[oO][yY])?)))?)([ -]([fF][uU][eE][lL]))?|(([iI][cC]2[ -]?)?([eE][nN][rR][iI][cC][hH][eE][dD] )?[a-zA-Z]+([ -]?([oO][xX]([iI][dD][eE])?|[nN][iI]([tT][rR][iI][dD][eE])?|([tT][eE][tT][rR][aA])?[fF](4|[lL][uU][oO][rR][iI][dD][eE])|[zZ]([aA]|[iI][rR][cC][oO][nN][iI][uU][mM]([ -]?[aA][lL]{2}[oO][yY])?))?)([ -](([nN][uU][cC][lL][eE][aA][rR] )?[fF][uU][eE][lL]|[iI][nN][gG][oO][tT])))|[yY][eE][lL]{2}[oO][rR][iI][uU][mM]|([eE][nN][rR][iI][cC][hH][eE][dD][ -])?[uU][rR][aA][nN][iI][uU][mM]";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordFuel();
    }
}