package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class SilverSmore extends HutThing{
    public SilverSmore(){
        super("Silver S'more Trophy", "silver smore", -1);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new SilverSmore();
    }
}