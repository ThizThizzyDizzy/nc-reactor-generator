package discord.play;
import java.io.File;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import simplelibrary.config2.Config;
public class PlayBot{
    public static HashMap<Long, Long> smores = new HashMap<>();//MONEY
    public static HashMap<Long, Long> eaten = new HashMap<>();//MONEY
    public static HashMap<Long, Action> actions = new HashMap<>();
    public static void action(GuildMessageReceivedEvent event, Action action){
        if(actions.containsKey(event.getAuthor().getIdLong())){
            event.getChannel().sendMessage("You can't do that many things at once!").queue();
            return;
        }
        actions.put(event.getAuthor().getIdLong(), action);
        action.start(event.getAuthor(), event.getChannel());
    }
    public static long getSmoreCount(long id){
        return smores.containsKey(id)?smores.get(id):0;
    }
    public static String getSmoreCountS(long id){
        return "<:smore:493612965195677706> "+getSmoreCount(id);
    }
    public static long getEatenCount(long id){
        return eaten.containsKey(id)?eaten.get(id):0;
    }
    public static String getEatenCountS(long id){
        return "<:smore:493612965195677706> "+getEatenCount(id);
    }
    public static void save(){
        File f = new File("smorebank.dat").getAbsoluteFile();
        Config config = Config.newConfig(f);
        Config bank = Config.newConfig();
        for(Long id : smores.keySet()){
            bank.set(id+"", smores.get(id));
        }
        config.set("bank", bank);
        Config yums = Config.newConfig();
        for(Long id : eaten.keySet()){
            yums.set(id+"", eaten.get(id));
        }
        config.set("yums", yums);
        config.save();
    }
    public static void load(){
        File f = new File("smorebank.dat").getAbsoluteFile();
        if(!f.exists())return;
        Config config = Config.newConfig(f);
        config.load();
        Config bank = config.get("bank", Config.newConfig());
        for(String key : bank.properties()){
            smores.put(Long.parseLong(key), bank.get(key));
        }
        Config yums = config.get("yums", Config.newConfig());
        for(String key : yums.properties()){
            eaten.put(Long.parseLong(key), yums.get(key));
        }
    }
    public static void addSmore(User user){
        addSmores(user, 1);
    }
    public static void addSmores(User user, long count){
        if(count<0)throw new IllegalArgumentException("You can't add a negative amount of s'mores! use `removeSmores` instead!");
        if(count==0)throw new IllegalArgumentException("You can't add no s'mores!");
        long id = user.getIdLong();
        if(smores.containsKey(id)){
            smores.put(id, smores.get(id)+count);
        }else{
            smores.put(id, count);
        }
    }
    public static void removeSmores(User user, long count){
        if(count<0)throw new IllegalArgumentException("You can't remove a negative amount of s'mores! use `addSmores` instead!");
        if(count==0)throw new IllegalArgumentException("You can't remove no s'mores!");
        long id = user.getIdLong();
        if(smores.containsKey(id)){
            smores.put(id, smores.get(id)-count);
        }else{
            smores.put(id, -count);
        }
    }
    public static void eatSmores(User author, long count){
        removeSmores(author, count);
        long id = author.getIdLong();
        if(eaten.containsKey(id)){
            eaten.put(id, eaten.get(id)+count);
        }else{
            eaten.put(id, count);
        }
    }
}