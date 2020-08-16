package discord.play.smivilization.thing;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import discord.play.smivilization.HutThingColorable;
import java.awt.Color;
import java.util.UUID;
public class Couch extends HutThingColorable{
    public Couch(UUID uuid, Hut hut){
        super(uuid, hut, "Couch", "couch", 16, Color.red);
    }
    @Override
    public HutThing newInstance(UUID uuid, Hut hut){
        return new Couch(uuid, hut);
    }
}