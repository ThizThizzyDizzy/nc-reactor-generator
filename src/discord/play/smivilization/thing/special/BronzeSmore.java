package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class BronzeSmore extends HutThing{
    public BronzeSmore(){
        super("Bronze S'more Trophy", "trophy/bronze smore", -1);
        require("Shelf");
    }
    @Override
    public HutThing newInstance(){
        return new BronzeSmore();
    }
    @Override
    public int getLayer(){
        return -1;
    }
}