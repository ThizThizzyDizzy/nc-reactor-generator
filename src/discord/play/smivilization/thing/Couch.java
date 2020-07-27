package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import java.awt.Color;
public class Couch extends HutThingColorable{
    public Couch(){
        super("Couch", "couch", 16, Color.red);
    }
    @Override
    public HutThing newInstance(){
        return new Couch();
    }
}