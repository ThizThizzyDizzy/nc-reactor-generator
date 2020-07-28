package discord.play.smivilization.thing;
import discord.play.smivilization.HutThing;
public class Television extends HutThing{
    public Television(){
        super("Television", "tv", 32);
        require("coffee table");
    }
    @Override
    public HutThing newInstance(){
        return new Television();
    }
}