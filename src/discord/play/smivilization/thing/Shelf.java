package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class Shelf extends HutThing{
    public Shelf(UUID uuid, Hut hut){
        super(uuid, hut, "Shelf", "shelf", 10);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Shelf(uuid, hut);
    }
}