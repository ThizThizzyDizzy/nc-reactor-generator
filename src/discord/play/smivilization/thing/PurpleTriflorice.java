package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class PurpleTriflorice extends HutThing{
    public PurpleTriflorice(UUID uuid, Hut hut){
        super(uuid, hut, "Purple Triflorice", "purple triflorice", 4);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new PurpleTriflorice(uuid, hut);
    }
}