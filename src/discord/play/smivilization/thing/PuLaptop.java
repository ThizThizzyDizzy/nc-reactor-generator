package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class PuLaptop extends HutThing{
    public PuLaptop(){
        super("Pu Laptop", "pu laptop", 32);
        require("Table");
    }
    @Override
    public HutThing newInstance(){
        return new PuLaptop();
    }
}