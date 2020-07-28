package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class Shelf extends HutThing{
    public Shelf(){
        super("Shelf", "shelf", 10);
    }
    @Override
    public HutThing newInstance(){
        return new Shelf();
    }
}