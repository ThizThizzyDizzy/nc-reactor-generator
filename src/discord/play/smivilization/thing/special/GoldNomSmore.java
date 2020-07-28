package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class GoldNomSmore extends HutThing{
    public GoldNomSmore(){
        super("Gold Eaten S'more Trophy", "trophy/gold eaten smore", -1);
        require("Shelf");
    }
    @Override
    public HutThing newInstance(){
        return new GoldNomSmore();
    }
    @Override
    public int getLayer(){
        return 0;
    }
}