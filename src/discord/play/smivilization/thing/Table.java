package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class Table extends HutThing{
    public Table(){
        super("Table", "table", 8);
    }
    @Override
    public HutThing newInstance(){
        return new Table();
    }
}