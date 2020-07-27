package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class PurpleTriflorice extends HutThing{
    public PurpleTriflorice(){
        super("Purple Triflorice", "purple triflorice", 4);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new PurpleTriflorice();
    }
}