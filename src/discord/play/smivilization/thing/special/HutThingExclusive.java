package discord.play.smivilization.thing.special;
import discord.play.smivilization.Hut;
import discord.play.smivilization.HutThing;
import java.util.UUID;
public abstract class HutThingExclusive extends HutThing{
    public final long exclusiveOwner;
    public HutThingExclusive(UUID uuid, Hut hut, String name, String textureName, long exclusiveOwner, long price){
        super(uuid, hut, name, textureName, price);
        this.exclusiveOwner = exclusiveOwner;
    }
}