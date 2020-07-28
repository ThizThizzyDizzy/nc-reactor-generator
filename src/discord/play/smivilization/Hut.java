package discord.play.smivilization;
import discord.play.smivilization.thing.*;
import discord.Bot;
import discord.play.SmoreBot;
import discord.play.smivilization.thing.special.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.CircularStream;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class Hut{
    public static long PRICE = 32;
    private final long owner;
    public static final ArrayList<HutThing> allFurniture = new ArrayList<>();
    static{
        allFurniture.add(new Lamp());
        allFurniture.add(new Shelf());
        allFurniture.add(new Couch());
        allFurniture.add(new Bed());
        allFurniture.add(new PurpleTriflorice());
        allFurniture.add(new PuLaptop());
        allFurniture.add(new Table());
        allFurniture.add(new Television());
        allFurniture.add(new CoffeeTable());
        allFurniture.add(new TomPainting());
        allFurniture.add(new GlowshroomGlowshroomGlowshroomPoster());
        allFurniture.add(new SmoreRug());
        for(HutThing thing : allFurniture){
            thing.postRequire();
        }
    }
    public ArrayList<Long> invited = new ArrayList<>();
    public ArrayList<HutThing> furniture = new ArrayList<>();
    public boolean open;
    public Hut(long owner){
        this.owner = owner;
        if(owner==210445638532333569l/*210340018198151170l*/){
            furniture.add(new GlowshroomGlowshroomGlowshroomPoster());
        }
    }
    public Config save(Config config){
        config.set("owner", owner);
        ConfigNumberList inviteds = new ConfigNumberList();
        for(Long l : invited){
            inviteds.add(l);
        }
        config.set("invited", inviteds);
        ConfigList furn = new ConfigList();
        for(HutThing thing : furniture){
            furn.add(thing.save(Config.newConfig()));
        }
        config.set("furniture", furn);
        config.set("open", open);
        return config;
    }
    public static Hut load(Config config){
        Hut hut = new Hut(config.get("owner"));
        ConfigNumberList inviteds = config.get("invited");
        for(int i = 0; i<inviteds.size(); i++){
            hut.invited.add(inviteds.get(i));
        }
        ConfigList furn = config.get("furniture", new ConfigList());
        for(Object c : furn.iterable()){
            hut.furniture.add(HutThing.load((Config)c));
        }
        hut.open = config.get("open", false);
        return hut;
    }
    public boolean isAllowedInside(User user){
        if(open)return true;
        return invited.contains(user.getIdLong());
    }
    public void sendImage(TextChannel channel, String name, Core.BufferRenderer renderer){
        sendImage(channel, name, 512, 512, renderer);
    }
    public void sendImage(TextChannel channel, String name, int width, int height, Core.BufferRenderer renderer){
        CircularStream stream = new CircularStream(1024*1024);//1MB
        CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), name+".png").submit();
        try{
            ImageIO.write(Bot.makeImage(width, height, renderer), "png", stream);
            stream.close();
        }catch(Exception ex){
            Bot.printErrorMessage(channel, "Failed to write file", ex);
            submit.cancel(true);
            stream.close();
        }
    }
    public void sendExteriorImage(TextChannel channel){
        sendImage(channel, "outside", (buff) -> {
            Renderer2D.drawRect(0, 0, buff.width, buff.height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/outside.png"));
        });
    }
    public void sendInteriorImage(TextChannel channel){
        sendImage(channel, "inside", (buff) -> {
            Renderer2D.drawRect(0, 0, buff.width, buff.height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/inside.png"));
            ArrayList<HutThing> furn = new ArrayList<>(furniture);
            //<editor-fold defaultstate="collapsed" desc="Nom Trophy">
            HutThing nomTrophy = null;
            switch(SmoreBot.getEatenPlacement(owner)){
                case 1:
                    nomTrophy = new GoldNomSmore();
                    break;
                case 2:
                    nomTrophy = new SilverNomSmore();
                    break;
                case 3:
                    nomTrophy = new BronzeNomSmore();
                    break;
            }
            if(nomTrophy!=null){
                nomTrophy.postRequire();
                boolean canFit = true;
                for(HutThing required : nomTrophy.getRequirements()){
                    boolean has = false;
                    for(HutThing thing : furn){
                        if(thing.equals(required))has = true;
                    }
                    if(!has)canFit = false;
                }
                if(canFit){
                    furn.add(nomTrophy);
                }
            }
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Trophy">
            HutThing trophy = null;
            switch(SmoreBot.getSmorePlacement(owner)){
                case 1:
                    trophy = new GoldSmore();
                    break;
                case 2:
                    trophy = new SilverSmore();
                    break;
                case 3:
                    trophy = new BronzeSmore();
                    break;
            }
            if(trophy!=null){
                trophy.postRequire();
                boolean canFit = true;
                for(HutThing required : trophy.getRequirements()){
                    boolean has = false;
                    for(HutThing thing : furn){
                        if(thing.equals(required))has = true;
                    }
                    if(!has)canFit = false;
                }
                if(canFit){
                    furn.add(trophy);
                }
            }
//</editor-fold>
            Collections.sort(furn, (o1, o2) -> {
                return o2.getLayer()-o1.getLayer();
            });
            for(HutThing thing : furn){
                GL11.glColor4d(1, 1, 1, 1);
                thing.render(buff.width, buff.height);
            }
        });
    }
}