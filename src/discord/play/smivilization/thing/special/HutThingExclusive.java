package discord.play.smivilization.thing.special;
import discord.play.smivilization.HutThing;
public abstract class HutThingExclusive extends HutThing{
    public final long exclusiveOwner;
    public HutThingExclusive(String name, String textureName, long exclusiveOwner, long price){
        super(name, textureName, price);
        this.exclusiveOwner = exclusiveOwner;
    }
}