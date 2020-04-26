package discord;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class Bot extends ListenerAdapter{
    private static JDA jda;
    private static boolean running = false;
    private static String[] prefixes;
    private static final long TIME_LIMIT = 1_000_000_000l*60;//one minute in nanoseconds
    private static long overhaulTime, underhaulTime;
    private int MAX_SIZE = 128; //the biggest allowed value for the X/Y/Z dimensions
    private Message overhaulMessage;
    private Message underhaulMessage;
    private ArrayList<CompletableFuture<Message>> underhaulFutures = new ArrayList<>();
    private ArrayList<CompletableFuture<Message>> overhaulFutures = new ArrayList<>();
    public static void start(String[] args) throws LoginException{
        if(args.length>2){
            prefixes = new String[args.length-2];
            for(int i = 0; i<prefixes.length; i++){
                prefixes[i] = args[i+2];
            }
        }else{
            prefixes = new String[]{"-"};
        }
        JDABuilder b = new JDABuilder(AccountType.BOT);
        b.setToken(args[1]);
        b.addEventListeners(new Bot());
        jda = b.build();
        running = true;
    }
    static void stop(){
        if(running)jda.shutdownNow();
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent mre){
        Message message = mre.getMessage();
        String content = message.getContentStripped();
        for(String prefix : prefixes){
            if(!content.startsWith(prefix))continue;
            content = content.substring(prefix.length());
            content = content.toLowerCase().replace("_", "").replace("-", "").replace(":", "").replace("=", "");
            while(content.contains("  "))content = content.replace("  ", " ");
            content = content.replace(" x ", "x");
            if(content.startsWith("abort")){
                if(content.contains("overhaul")&&!content.contains("preoverhaul")&&!content.contains("underhaul")){
                    if(overhaul.Main.running){
                        overhaul.Main.instance.stop();
                        message.getChannel().sendMessage("Overhaul generation halted").queue();
                    }
                }else{
                    if(pre_overhaul.Main.running){
                        pre_overhaul.Main.instance.stop();
                        message.getChannel().sendMessage("Underhaul generation halted").queue();
                    }
                }
            }
            if(content.startsWith("generate")||content.startsWith("search")||content.startsWith("find")){
                if(content.contains("overhaul")&&!content.contains("preoverhaul")&&!content.contains("underhaul")){
                    if(overhaul.Main.running){
                        message.getChannel().sendMessage("Overhaul generator is already running!").queue();
                        return;
                    }
                    int X = 5,Y = 5,Z = 5;
                    for(int x = 0; x<MAX_SIZE; x++){
                        for(int y = 0; y<MAX_SIZE; y++){
                            for(int z = 0; z<MAX_SIZE; z++){
                                if(content.contains(x+"x"+y+"x"+z)){
                                    X = x;
                                    Y = y;
                                    Z = z;
                                }
                            }
                        }
                    }
                    overhaul.Fuel fuel = overhaul.Fuel.fuels.get(3);
                    boolean fuelSet = false;
                    for(overhaul.Fuel f : overhaul.Fuel.fuels){
                        if(content.contains(f.toString().toLowerCase().replace("-", ""))){
                            content = content.replace(f.toString().toLowerCase().replace("-", ""), "");
                            if(fuelSet){
                                message.getChannel().sendMessage("Reactors with multiple fuels are not supported!").queue();
                                return;
                            }
                            fuel = f;
                            fuelSet = true;
                        }
                    }
                    overhaul.Fuel.Type type = overhaul.Fuel.Type.OX;
                    boolean typeSet = false;
                    for(overhaul.Fuel.Type f : overhaul.Fuel.Type.values()){
                        if(content.contains(f.name().toLowerCase().replace(" ", "").replace("-", ""))){
                            content = content.replace(f.name().toLowerCase().replace(" ", "").replace("-", ""), "");
                            if(typeSet){
                                message.getChannel().sendMessage("Reactors with multiple fuels are not supported!").queue();
                                return;
                            }
                            type = f;
                            typeSet = true;
                        }
                        if(content.contains(f.toString().toLowerCase().replace("-", ""))){
                            content = content.replace(f.toString().toLowerCase().replace("-", ""), "");
                            if(typeSet){
                                message.getChannel().sendMessage("Reactors with multiple fuels are not supported!").queue();
                                return;
                            }
                            type = f;
                            typeSet = true;
                        }
                    }
                    if(content.contains("efficiency")||content.contains("efficient")){
                        overhaul.Priority.moveToEnd("Output");
                    }else if(content.contains("output")){
                        overhaul.Priority.moveToEnd("Efficiency");
                    }else{//default
                        overhaul.Priority.moveToEnd("Efficiency");
                    }
                    if(overhaul.Main.instance!=null)overhaul.Main.instance.dispose();
                    overhaul.Main.instance = new overhaul.Main();
                    overhaul.Main.instance.setVisible(true);
                    overhaul.Main.instance.spinnerX.setValue(X);
                    overhaul.Main.instance.spinnerY.setValue(Y);
                    overhaul.Main.instance.spinnerZ.setValue(Z);
                    overhaul.Main.instance.boxFuel.setSelectedIndex(overhaul.Fuel.fuels.indexOf(fuel));
                    overhaul.Main.instance.boxFuelType.setSelectedIndex(type.ordinal());
                    overhaul.Main.instance.start();
                    try{
                        overhaulMessage = message.getChannel().sendMessage(new EmbedBuilder().setTitle("Generating Reactors...").build()).complete();
                    }catch(InsufficientPermissionException ex){
                        overhaulMessage = message.getChannel().sendMessage("Generating Reactors...").complete();
                    }
                    overhaulTime = System.nanoTime();
                    Thread t = new Thread(() -> {
                        while(overhaul.Main.running&&System.nanoTime()<overhaulTime+TIME_LIMIT){
                            try{
                                Thread.sleep(1000);
                                updateOverhaul("Generating reactors...\n");
                            }catch(InterruptedException ex){
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(overhaul.Main.running)overhaul.Main.instance.stop();
                        updateOverhaul("Generated Reactor");
                    });
                    t.setDaemon(true);
                    t.start();
                }else{
                    if(pre_overhaul.Main.running){
                        message.getChannel().sendMessage("Underhaul generator is already running!").queue();
                        return;
                    }
                    int X = 3,Y = 3,Z = 3;
                    for(int x = 0; x<MAX_SIZE; x++){
                        for(int y = 0; y<MAX_SIZE; y++){
                            for(int z = 0; z<MAX_SIZE; z++){
                                if(content.contains(x+"x"+y+"x"+z)){
                                    X = x;
                                    Y = y;
                                    Z = z;
                                }
                            }
                        }
                    }
                    pre_overhaul.Fuel fuel = pre_overhaul.Fuel.fuels.get(7);
                    boolean fuelSet = false;
                    for(pre_overhaul.Fuel f : pre_overhaul.Fuel.fuels){
                        if(!f.toString().contains("Oxide"))continue;
                        if(content.contains(f.toString().toLowerCase().replace("-", ""))){
                            content = content.replace(f.toString().toLowerCase().replace("-", ""), "");
                            if(fuelSet){
                                message.getChannel().sendMessage("Reactors with multiple fuels are not supported!").queue();
                                return;
                            }
                            fuel = f;
                            fuelSet = true;
                        }
                    }
                    for(pre_overhaul.Fuel f : pre_overhaul.Fuel.fuels){
                        if(f.toString().contains("Oxide"))continue;
                        if(content.contains(f.toString().toLowerCase().replace("-", ""))){
                            content = content.replace(f.toString().toLowerCase().replace("-", ""), "");
                            if(fuelSet){
                                message.getChannel().sendMessage("Reactors with multiple fuels are not supported!").queue();
                                return;
                            }
                            fuel = f;
                            fuelSet = true;
                        }
                    }
                    if(content.contains("efficiency")||content.contains("efficient")){
                        pre_overhaul.Priority.moveToEnd("Output");
                        pre_overhaul.Priority.moveToEnd("Minimize Heat");
                        pre_overhaul.Priority.moveToEnd("Fuel Usage");
                        pre_overhaul.Priority.moveToEnd("Cell Count");
                    }else if(content.contains("output")){
                        pre_overhaul.Priority.moveToEnd("Efficiency");
                        pre_overhaul.Priority.moveToEnd("Minimize Heat");
                        pre_overhaul.Priority.moveToEnd("Fuel Usage");
                        pre_overhaul.Priority.moveToEnd("Cell Count");
                    }else if(content.contains("breeder")){
                        pre_overhaul.Priority.moveToEnd("Fuel Usage");
                        pre_overhaul.Priority.moveToEnd("Minimize Heat");
                        pre_overhaul.Priority.moveToEnd("Output");
                        pre_overhaul.Priority.moveToEnd("Efficiency");
                        pre_overhaul.Priority.moveToEnd("Cell Count");
                    }else{//default
                        pre_overhaul.Priority.moveToEnd("Output");
                        pre_overhaul.Priority.moveToEnd("Minimize Heat");
                        pre_overhaul.Priority.moveToEnd("Fuel Usage");
                        pre_overhaul.Priority.moveToEnd("Cell Count");
                    }
                    if(pre_overhaul.Main.instance!=null)pre_overhaul.Main.instance.dispose();
                    pre_overhaul.Main.instance = new pre_overhaul.Main();
                    pre_overhaul.Main.instance.setVisible(true);
                    pre_overhaul.Main.instance.spinnerX.setValue(X);
                    pre_overhaul.Main.instance.spinnerY.setValue(Y);
                    pre_overhaul.Main.instance.spinnerZ.setValue(Z);
                    pre_overhaul.Main.instance.boxFuel.setSelectedIndex(pre_overhaul.Fuel.fuels.indexOf(fuel));
                    pre_overhaul.Main.instance.start();
                    try{
                        underhaulMessage = message.getChannel().sendMessage(new EmbedBuilder().setTitle("Generating Reactors...").build()).complete();
                    }catch(InsufficientPermissionException ex){
                        underhaulMessage = message.getChannel().sendMessage("Generating Reactors...").complete();
                    }
                    underhaulTime = System.nanoTime();
                    Thread t = new Thread(() -> {
                        while(pre_overhaul.Main.running&&System.nanoTime()<underhaulTime+TIME_LIMIT){
                            try{
                                Thread.sleep(1000);
                                updateUnderhaul("Generating reactors...\n");
                            }catch(InterruptedException ex){
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(pre_overhaul.Main.running)pre_overhaul.Main.instance.stop();
                        updateUnderhaul("Generated Reactor");
                    });
                    t.setDaemon(true);
                    t.start();
                }
            }
            break;
        }
    }
    private void updateOverhaul(String prefix){
        for(CompletableFuture<Message> future : overhaulFutures){
            if(!future.isDone())future.cancel(false);
        }
        overhaulFutures.clear();
        String text = "";
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(prefix);
        text+="**"+prefix+"**\n";
        overhaul.Reactor r = overhaul.Main.genPlan.getReactors().get(0);
        String priorities = "";
        for(overhaul.Priority p : overhaul.Priority.priorities){
            priorities+="\n"+p.toString();
        }
        builder.addField("Priorities", priorities.substring(1), false);
        text+="\n**Priorities**\n"+priorities;
        String details = "Size: "+overhaul.Main.instance.spinnerX.getValue()+"x"+overhaul.Main.instance.spinnerY.getValue()+"x"+overhaul.Main.instance.spinnerZ.getValue()+"\n"
                + "Fuel: "+overhaul.Fuel.fuels.get(overhaul.Main.instance.boxFuel.getSelectedIndex())+" "+overhaul.Fuel.Type.values()[overhaul.Main.instance.boxFuelType.getSelectedIndex()]+"\n"+r.getDetails(false, false);
        builder.addField("Details", details, false);
        text+="\n**Details**\n"+details;
        String layout = "";
        for(int y = 0; y<r.y; y++){
            for(int z = 0; z<r.z; z++){
                for(int x = 0; x<r.x; x++){
                    layout += toEmoteString(r.parts[x][y][z], overhaulMessage.getGuild());
                }
                layout+="\n";
            }
            layout+="\n";
        }
        builder.addField("Reactor Layout", layout.length()>1024?"Too big!":layout, true);
        text+="\n**Reactor Layout**\n"+layout;
        if(text.length()>2000)text = text.substring(0, text.indexOf("Reactor Layout"))+"Reactor Layout**\n<Too big>";
        builder.setColor(Color.ORANGE);
        builder.setFooter("Overhaul | Stability of multi-cluser reactors is not guaranteed");
        try{
            overhaulFutures.add(overhaulMessage.editMessage(builder.build()).submit());
        }catch(InsufficientPermissionException ex){
            overhaulFutures.add(overhaulMessage.editMessage(text).submit());
        }
    }
    private void updateUnderhaul(String prefix){
        for(CompletableFuture<Message> future : underhaulFutures){
            if(!future.isDone())future.cancel(false);
        }
        underhaulFutures.clear();
        String text = "";
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(prefix);
        text+="**"+prefix+"**\n";
        pre_overhaul.Reactor r = pre_overhaul.Main.genPlan.getReactors().get(0);
        String priorities = "";
        for(pre_overhaul.Priority p : pre_overhaul.Priority.priorities){
            priorities+="\n"+p.toString();
        }
        builder.addField("Priorities", priorities.substring(1), false);
        text+="\n**Priorities**\n"+priorities.substring(1);
        String details = "Size: "+pre_overhaul.Main.instance.spinnerX.getValue()+"x"+pre_overhaul.Main.instance.spinnerY.getValue()+"x"+pre_overhaul.Main.instance.spinnerZ.getValue()+"\n"
                + "Fuel: "+pre_overhaul.Fuel.fuels.get(pre_overhaul.Main.instance.boxFuel.getSelectedIndex())+"\n"+r.getDetails(false);
        builder.addField("Details", details, false);
        text+="\n**Details**\n"+details.substring(1);
        String layout = "";
        for(int y = 0; y<r.y; y++){
            for(int z = 0; z<r.z; z++){
                for(int x = 0; x<r.x; x++){
                    layout+=toEmoteString(r.parts[x][y][z], underhaulMessage.getGuild());
                }
                layout+="\n";
            }
            layout+="\n";
        }
        builder.addField("Reactor Layout", layout.length()>1024?"Too Big!":layout, true);
        text+="\n**Reactor Layout**\n"+layout;
        if(text.length()>2000)text = text.substring(0, text.indexOf("Reactor Layout"))+"Reactor Layout**\n<Too big>";
        builder.setColor(Color.ORANGE);
        builder.setFooter("Underhaul");
        try{
            underhaulFutures.add(underhaulMessage.editMessage(builder.build()).submit());
        }catch(InsufficientPermissionException ex){
            underhaulFutures.add(underhaulMessage.editMessage(text).submit());
        }
    }
    private String toEmoteString(pre_overhaul.ReactorPart part, Guild guild){
        String s = toEString(part);
        List<Emote> emotes = guild.getEmotesByName(s, true);
        if(emotes.isEmpty())return s;
        return emotes.get(0).getAsMention();
    }
    private String toEmoteString(overhaul.ReactorPart part, Guild guild){
        String s = toEString(part);
        List<Emote> emotes = guild.getEmotesByName(s, true);
        if(emotes.isEmpty())return s;
        return emotes.get(0).getAsMention();
    }
    private String toEString(pre_overhaul.ReactorPart part){
        switch(part.type){
            case AIR:
                return "air";
            case CASING:
                return "Casing";
            case COOLER:
                return part.toString().toLowerCase().replace(" ", "_").replace("_cooler", "");
            case FUEL_CELL:
                return "cell";
            case MODERATOR:
                return "graphite";
        }
        return part.toString();
    }
    private String toEString(overhaul.ReactorPart part){
        switch(part.type){
            case AIR:
                return "air";
            case CASING:
                return "Casing";
            case CONDUCTOR:
                return "conductor";
            case FUEL_CELL:
                return "cell";
            case REFLECTOR:
                return "reflector";
            case HEATSINK:
                return part.toString().toLowerCase().replace(" ", "_").replace("_heatsink", "");
            case MODERATOR:
                return part.toString().toLowerCase().replace(" ", "_").replace("_moderator", "");
        }
        return part.toString();
    }
}