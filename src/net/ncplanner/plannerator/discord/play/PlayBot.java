package net.ncplanner.plannerator.discord.play;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.MessageChannel;
public class PlayBot{
    public static int maxGames = 10;
    public static HashMap<Long, Game> games = new HashMap<>();
    public static void play(MessageChannel channel, Game game){
        if(games.size()>=maxGames){
            channel.sendMessage("There are too many games running! Please wait for a game to finish before starting another.").queue();
            return;
        }
        if(games.containsKey(channel.getIdLong())){
            channel.sendMessage("There is already an active game in this channel!").queue();
            return;
        }
        games.put(channel.getIdLong(), game);
        game.startGame(channel);
    }
}