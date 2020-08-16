package discord.play;
import net.dv8tion.jda.api.entities.MessageChannel;
public class PlayBot{
    public static Game currentGame = null;
    public static void play(MessageChannel channel, Game game){
        if(currentGame!=null){
            channel.sendMessage("A game of "+currentGame.getName()+" is already in progress!").queue();
            return;
        }
        currentGame = game;
        game.startGame(channel);
    }
}