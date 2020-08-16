package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class CoffeeTable extends HutThing{
    public CoffeeTable(UUID uuid, Hut hut){
        super(uuid, hut, "Coffee Table", "coffee table", 16);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new CoffeeTable(uuid, hut);
    }
}