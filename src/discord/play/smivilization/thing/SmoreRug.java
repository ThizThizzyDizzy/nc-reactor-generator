package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import java.awt.Color;
public class SmoreRug extends HutThingColorable{
    public SmoreRug(){
        super("S'more Rug", "smore rug", 8, Color.white);
    }
    @Override
    public HutThing newInstance(){
        return new SmoreRug();
    }
}