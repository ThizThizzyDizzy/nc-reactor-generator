package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class TomPainting extends HutThing{
    public TomPainting(){
        super("tomdodd4598 Painting", "tompainting", 4598);
    }
    @Override
    public HutThing newInstance(){
        return new TomPainting();
    }
}