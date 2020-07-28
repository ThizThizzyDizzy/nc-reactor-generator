package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class GoldSmore extends HutThing{
    public GoldSmore(){
        super("Gold S'more Trophy", "trophy/gold smore", -1);
        require("Shelf");
    }
    @Override
    public HutThing newInstance(){
        return new GoldSmore();
    }
    @Override
    public int getLayer(){
        return -1;
    }
}