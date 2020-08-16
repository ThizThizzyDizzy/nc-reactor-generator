package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class PuLaptop extends HutThing{
    public PuLaptop(UUID uuid, Hut hut){
        super(uuid, hut, "Pu Laptop", "pu laptop", 32);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new PuLaptop(uuid, hut);
    }
}