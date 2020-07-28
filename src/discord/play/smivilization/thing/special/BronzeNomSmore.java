package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class BronzeNomSmore extends HutThing{
    public BronzeNomSmore(){
        super("Bronze Eaten S'more Trophy", "trophy/bronze eaten smore", -1);
        require("Shelf");
    }
    @Override
    public HutThing newInstance(){
        return new BronzeNomSmore();
    }
    @Override
    public int getLayer(){
        return 0;
    }
}