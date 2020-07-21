package discord.keyword;
import discord.Keyword;
import java.awt.Color;
public class KeywordCuboid extends Keyword{
    public int x,y,z;
    public KeywordCuboid(){
        super("Cuboid");
    }
    @Override
    public String toString(){
        return "Cuboid: "+x+"x"+y+"x"+z;
    }
    @Override
    public Color getColor(){
        return new Color(255,0,0);
    }
    @Override
    public String getRegex(){
        return "\\d+[xX]\\d+[xX]\\d+";
    }
    @Override
    public Keyword newInstance(){
        return new KeywordCuboid();
    }
    @Override
    public boolean doRead(String input){
        String[] split = input.split("x");
        x = Integer.parseInt(split[0]);
        y = Integer.parseInt(split[1]);
        z = Integer.parseInt(split[2]);
        return true;
    }
}