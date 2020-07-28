package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class SilverNomSmore extends HutThing{
    public SilverNomSmore(){
        super("Silver Eaten S'more Trophy", "trophy/silver eaten smore", -1);
        require("Shelf");
    }
    @Override
    public HutThing newInstance(){
        return new SilverNomSmore();
    }
    @Override
    public int getLayer(){
        return 0;
    }
}