package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class BronzeNomSmore extends HutThing{
    public BronzeNomSmore(){
        super("Bronze Eaten S'more Trophy", "bronze eaten smore", -1);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new BronzeNomSmore();
    }
}