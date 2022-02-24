package net.ncplanner.plannerator.discord.play;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.discord.play.smivilization.Hut;
import net.ncplanner.plannerator.discord.play.smivilization.HutBunch;
import net.ncplanner.plannerator.discord.play.smivilization.HutType;
public class SmoreBot{
    public static HashMap<Long, Long> glowshrooms = new HashMap<>();
    public static HashMap<Long, Long> smores = new HashMap<>();//MONEY
    public static HashMap<Long, Long> eaten = new HashMap<>();//MONEY
    public static HashMap<Long, Action> actions = new HashMap<>();
    public static HashMap<Long, HutBunch> hutBunches = new HashMap<>();
    public static void action(User user, MessageChannel channel, Action action){
        if(actions.containsKey(user.getIdLong())){
            channel.sendMessage("You can't do that many things at once!").queue();
            return;
        }
        actions.put(user.getIdLong(), action);
        action.start(user, channel);
    }
    public static long getSmoreCount(long id){
        return smores.containsKey(id)?smores.get(id):0;
    }
    public static String getSmoreCountS(long id){
        return "<:smore:493612965195677706> "+getSmoreCount(id);
    }
    public static long getGlowshroomCount(long id){
        return glowshrooms.containsKey(id)?glowshrooms.get(id):0;
    }
    public static String getGlowshroomCountS(long id){
        return "<:glowshroom:722491690094690324> "+getGlowshroomCount(id);
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
        Config glows = Config.newConfig();
        for(Long id : glowshrooms.keySet()){
            glows.set(id+"", glowshrooms.get(id));
        }
        config.set("glows", glows);
        Config theHuts = Config.newConfig();
        for(Long id : hutBunches.keySet()){
            theHuts.set(id+"", hutBunches.get(id).save(Config.newConfig()));
        }
        config.set("huts", theHuts);
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
        Config glows = config.get("glows", Config.newConfig());
        for(String key : glows.properties()){
            glowshrooms.put(Long.parseLong(key), glows.get(key));
        }
        Config theHuts = config.get("huts", Config.newConfig());
        for(String key : theHuts.properties()){
            hutBunches.put(Long.parseLong(key), HutBunch.load(theHuts.get(key)));
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
    public static void addGlowshroom(User user){
        addGlowshrooms(user, 1);
    }
    public static void addGlowshrooms(User user, long count){
        if(count<0)throw new IllegalArgumentException("You can't add a negative amount of glowshrooms! use `removeGlowshrooms` instead!");
        if(count==0)throw new IllegalArgumentException("You can't add no glowshrooms!");
        long id = user.getIdLong();
        if(glowshrooms.containsKey(id)){
            glowshrooms.put(id, glowshrooms.get(id)+count);
        }else{
            glowshrooms.put(id, count);
        }
    }
    public static void removeGlowshrooms(User user, long count){
        if(count<0)throw new IllegalArgumentException("You can't remove a negative amount of glowshrooms! use `addGlowshrooms` instead!");
        if(count==0)throw new IllegalArgumentException("You can't remove no glowshrooms!");
        long id = user.getIdLong();
        if(glowshrooms.containsKey(id)){
            glowshrooms.put(id, glowshrooms.get(id)-count);
        }else{
            glowshrooms.put(id, -count);
        }
    }
    public static int getSmorePlacement(long owner){
        ArrayList<Long> smorepilers = new ArrayList<>(smores.keySet());
        Collections.sort(smorepilers, (Long o1, Long o2) -> (int)(smores.get(o2)-smores.get(o1)));
        return smorepilers.indexOf(owner)+1;
    }
    public static int getEatenPlacement(long owner){
        ArrayList<Long> smorepilers = new ArrayList<>(eaten.keySet());
        Collections.sort(smorepilers, (Long o1, Long o2) -> (int)(eaten.get(o2)-eaten.get(o1)));
        return smorepilers.indexOf(owner)+1;
    }
    public static Hut getHut(long user){
        HutBunch bunch = hutBunches.get(user);
        if(bunch==null)return null;
        return bunch.huts.get(bunch.mainHut);
    }
    public static boolean hasHut(long user, HutType type){
        if(hutBunches.containsKey(user)){
            for(Hut hut : hutBunches.get(user).huts){
                if(hut.type==type)return true;
            }
        }
        return false;
    }
    public static boolean hasHut(long user){
        return getHut(user)==null;
    }
    public static String getCampfire(long user){
        Hut hut = getHut(user);
        return hut==null?"campfire":hut.type.campfireName;
    }
}