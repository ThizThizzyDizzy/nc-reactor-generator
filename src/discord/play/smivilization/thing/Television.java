package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class Television extends HutThing{
    public Television(UUID uuid, Hut hut){
        super(uuid, hut, "Television", "tv", 32);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Television(uuid, hut);
    }
}