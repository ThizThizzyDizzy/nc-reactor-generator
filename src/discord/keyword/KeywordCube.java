package discord.keyword;
import discord.Keyword;
import simplelibrary.image.Color;
public class KeywordCube extends Keyword{
    public int size;
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
        return "\\d+x\\d+";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordCube();
    }
    @Override
    public boolean doRead(String input){
        String[] split = input.split("x");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        if(x!=y)return false;
        size = x;
        return true;
    }
    @Override
    public boolean caseSensitive(){
        return false;
    }
}