package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class Table extends HutThing{
    public Table(UUID uuid, Hut hut){
        super(uuid, hut, "Table", "table", 8);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Table(uuid, hut);
    }
}