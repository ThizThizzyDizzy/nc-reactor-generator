package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class SilverNomSmore extends HutThing{
    public SilverNomSmore(){
        super("Silver Eaten S'more Trophy", "silver eaten smore", -1);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new SilverNomSmore();
    }
}