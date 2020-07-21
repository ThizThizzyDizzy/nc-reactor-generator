package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordSymmetry extends Keyword{
    public String symmetry;
    public KeywordSymmetry(){
        super("Symmetry");
    }
    @Override
    public boolean doRead(String input){
        this.symmetry = input;
        return true;
    }
    @Override
    public Color getColor(){
        return new Color(127, 255, 0);
    }
    @Override
    public String getRegex(){
        return "([xXyYzZ]{1,3}[ -])?[sS][yY][mM]{2}[eE][tT][rR]([yY]|[iI][cC][aA][lL])";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordSymmetry();
    }
}