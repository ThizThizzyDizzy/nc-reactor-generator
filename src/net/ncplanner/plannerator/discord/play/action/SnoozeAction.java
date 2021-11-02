package net.ncplanner.plannerator.discord.play.action;
import java.util.Random;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.ncplanner.plannerator.discord.play.Action;
import net.ncplanner.plannerator.discord.play.SmoreBot;
public class SnoozeAction extends Action{
    public SnoozeAction(){
        super(new Random().nextInt(18000*60)+2000*60);//2-20 minutes
    }
    @Override
    protected String getBeginMessage(){
        return "You slowly doze off, the warmth of the "+SmoreBot.getCampfire(user.getIdLong())+" helping you to fall asleep.";
    }
    @Override
    protected String getFinishedMessage(){
        return user.getAsMention()+", You wake up"+(new Random().nextBoolean()?".":", feeling refreshed.");
    }
    @Override
    protected String getCanceledMessage(){
        return "You wake up, roll over, and fall back asleep.";
    }
    @Override
    protected String getName(){
        return "Sleeping";
    }
    @Override
    public void cancel(MessageChannel channel){
        super.cancel(channel);
        cancelled = false;
    }
    @Override
    public void finish(){}
    @Override
    public String getAction(){
        return "sleeping";
    }
}