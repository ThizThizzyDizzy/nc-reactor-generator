package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class BronzeSmore extends HutThing{
    public BronzeSmore(){
        super("Bronze S'more Trophy", "bronze smore", -1);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new BronzeSmore();
    }
}