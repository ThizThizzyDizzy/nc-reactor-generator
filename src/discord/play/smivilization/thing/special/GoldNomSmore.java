package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class GoldNomSmore extends HutThing{
    public GoldNomSmore(){
        super("Gold Eaten S'more Trophy", "gold eaten smore", -1);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new GoldNomSmore();
    }
}