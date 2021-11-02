package net.ncplanner.plannerator.discord;
import java.util.ArrayList;
import net.dv8tion.jda.api.entities.MessageChannel;
public abstract class Command{
    public final String command;
    public final ArrayList<String> alternates = new ArrayList<>();
    public Command(String command, String... alternates){
        this.command = command;
        this.alternates.add(command);
        for(String s : alternates)this.alternates.add(s);
    }
    public abstract String getHelpText();
    public boolean isSecret(){
        return false;
    }
    public boolean canRun(MessageChannel channel){
        return true;
    }
    public abstract void run(net.dv8tion.jda.api.entities.User user, MessageChannel channel, String args, boolean debug);
}