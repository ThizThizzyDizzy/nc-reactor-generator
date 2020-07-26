package discord.play;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
public class PlayBot{
    public static Game currentGame = null;
    public static void play(GuildMessageReceivedEvent event, Game game){
        if(currentGame!=null){
            event.getChannel().sendMessage("A game of "+currentGame.getName()+" is already in progress!").queue();
            return;
        }
        currentGame = game;
        game.startGame(event);
    }
}