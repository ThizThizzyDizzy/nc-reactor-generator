package discord;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
public abstract class Command{
    public final String command;
    public Command(String command){
        this.command = command;
    }
    public abstract String getHelpText();
    public boolean isSecret(){
        return false;
    }
    public boolean canRun(GuildMessageReceivedEvent event){
        return true;
    }
    public abstract void run(GuildMessageReceivedEvent event, String args, boolean debug);
}