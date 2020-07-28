package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class SilverSmore extends HutThing{
    public SilverSmore(){
        super("Silver S'more Trophy", "trophy/silver smore", -1);
        require("Shelf");
    }
    @Override
    public HutThing newInstance(){
        return new SilverSmore();
    }
    @Override
    public int getLayer(){
        return -1;
    }
}