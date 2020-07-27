package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class GoldSmore extends HutThing{
    public GoldSmore(){
        super("Gold S'more Trophy", "gold smore", -1);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new GoldSmore();
    }
}