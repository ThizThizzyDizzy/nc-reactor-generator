package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public class TomPainting extends HutThing{
    public TomPainting(UUID uuid, Hut hut){
        super(uuid, hut, "tomdodd4598 Painting", "tompainting", 4598);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new TomPainting(uuid, hut);
    }
}