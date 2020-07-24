package discord.play.action;
import discord.play.Action;
import discord.play.PlayBot;
import java.util.Random;
import net.dv8tion.jda.api.entities.TextChannel;
public class SmoreLordAction extends Action{
    public SmoreLordAction(){
        super((new Random().nextInt(4000*60)+8000*60)*9);//8-12 minutes, * 9
    }
    @Override
    protected String getBeginMessage(){
        return "You sit down by the campfire and cook a S'morelord. This could take quite a while.";
    }
    @Override
    protected String getFinishedMessage(){
        return user.getAsMention()+", Your S'morelord is done! It's absolutely majestic!";
    }
    @Override
    protected String getCanceledMessage(){
        return "You stop making your S'morelord, leaving it by the campfire to burn, reduced to atoms floating through the atmosphere, never to be seen again.\nThe fire grows into a massive bonfire, burning down a nearby hut\nYou are forced to pay 64 S'mores in damages";
    }
    @Override
    protected String getName(){
        return "S'morelords";
    }
    @Override
    public void finish(){
        PlayBot.addSmores(user, 8);
    }
    @Override
    public void cancel(TextChannel channel){
        PlayBot.addSmores(user, -64);
        super.cancel(channel);
    }
    @Override
    public String getAction(){
        return "cooking a S'morelord";
    }
}