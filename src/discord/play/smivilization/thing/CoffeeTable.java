package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class CoffeeTable extends HutThing{
    public CoffeeTable(){
        super("Coffee Table", "coffee table", 16);
    }
    @Override
    public HutThing newInstance(){
        return new CoffeeTable();
    }
}