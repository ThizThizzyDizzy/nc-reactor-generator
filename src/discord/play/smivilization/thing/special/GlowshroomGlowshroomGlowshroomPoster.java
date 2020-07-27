package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public class GlowshroomGlowshroomGlowshroomPoster extends HutThing{
    public GlowshroomGlowshroomGlowshroomPoster(){
        super("Glowhsroom Glowshroom Glowshroom Poster", "glowshroom poster", -1);
    }
    @Override
    public HutThing newInstance(){
        return new GlowshroomGlowshroomGlowshroomPoster();
    }
}