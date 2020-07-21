package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordCube extends Keyword{
    int size;
    public KeywordCube(){
        super("Square");
    }
    @Override
    public String toString(){
        return "Cube: "+size+"x"+size+"x"+size;
    }
    @Override
    public Color getColor(){
        return new Color(255,0,0);
    }
    @Override
    public String getRegex(){
        return "\\d+[xX]\\d+";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordCube();
    }
    @Override
    public boolean read(String input){
        String[] split = input.split("x");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        if(x!=y)return false;
        size = x;
        return true;
    }
}