package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import java.awt.Color;
import java.util.UUID;
public class SmoreRug extends HutThingColorable{
    public SmoreRug(UUID uuid, Hut hut){
        super(uuid, hut, "S'more Rug", "smore rug", 8, Color.white);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new SmoreRug(uuid, hut);
    }
}