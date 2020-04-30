package discord;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
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
            if(content.startsWith("help")){
                message.getChannel().sendMessage(getHelp()).queue();
                return;
            }
            if(content.startsWith("abort")||content.startsWith("halt")||content.startsWith("finish")||content.startsWith("stop")){
                if(content.contains("overhaul")&&!content.contains("preoverhaul")&&!content.contains("underhaul")){
                    if(overhaul.Main.running){
                        overhaul.Main.instance.stop();
                        message.getChannel().sendMessage("Overhaul generation halted").queue();
                    }else{
                        message.getChannel().sendMessage("Overhaul generator is not running!").queue();
                    }
                }else{
                    if(pre_overhaul.Main.running){
                        pre_overhaul.Main.instance.stop();
                        message.getChannel().sendMessage("Underhaul generation halted").queue();
                    }else{
                        message.getChannel().sendMessage("Underhaul generator is not running!").queue();
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
                    if(content.contains("symmetr")){
                        overhaul.Main.instance.checkBoxSymmetryX.setSelected(true);
                        overhaul.Main.instance.checkBoxSymmetryY.setSelected(true);
                        overhaul.Main.instance.checkBoxSymmetryZ.setSelected(true);
                    }
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
                    int sx = X, sy = Y, sz = Z;
                    overhaul.Fuel sf = fuel;
                    overhaul.Fuel.Type sft = type;
                    Thread t = new Thread(() -> {
                        while(overhaul.Main.running&&System.nanoTime()<overhaulTime+TIME_LIMIT){
                            try{
                                Thread.sleep(1000);
                                updateOverhaul("Generating reactors...\n", true);
                            }catch(InterruptedException ex){
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(overhaul.Main.running)overhaul.Main.instance.stop();
                        updateOverhaul("Generated Reactor", false);
                        File image = new File("overhaul.png");
                        File json = new File("overhaul.json");
                        overhaul.Reactor r = overhaul.Main.genPlan.getReactors().get(0);
                        try{
                            ImageIO.write(r.getImage(new Color(54, 57, 63)), "png", image);
                            message.getChannel().sendFile(image, "Underhaul "+sx+"x"+sy+"x"+sz+" "+sf.toString()+".png").queue();
                            r.exportJSON().write(json);
                            message.getChannel().sendFile(json, "Overhaul "+sx+"x"+sy+"x"+sz+" "+sf.toString()+" "+sft.toString()+".json").queue();
                        }catch(Exception ex){
                            message.getChannel().sendMessage(ex.getClass().getName()+": "+ex.getMessage()).queue();
                            ex.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                    for(Attachment at : message.getAttachments()){
                        try{
                            if(at.getFileExtension().equals("json")){
                                String text = "";
                                BufferedReader reader = new BufferedReader(new InputStreamReader(at.retrieveInputStream().get()));
                                String line;
                                while((line = reader.readLine())!=null){
                                    text+=line+"\n";
                                }
                                reader.close();
                                overhaul.Reactor r = overhaul.Reactor.parse(text, fuel, type, X, Y, Z);
                                if(r==null)throw new NullPointerException("Invalid Reactor");
                                overhaul.Main.genPlan.importReactor(r, true);
                                message.getChannel().sendMessage("Imported reactor: "+at.getFileName()).queue();
                            }else{
                                System.err.println("Unknown extention: "+at.getFileExtension());
                            }
                        }catch(Exception ex){
                            message.getChannel().sendMessage("Failed to parse attachment: "+at.getFileName()+"\n"+ex.getClass().getName()+": "+ex.getMessage()).queue();
                            ex.printStackTrace();
                        }
                    }
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
                    if(content.contains("symmetr")){
                        pre_overhaul.Main.instance.checkBoxSymmetryX.setSelected(true);
                        pre_overhaul.Main.instance.checkBoxSymmetryY.setSelected(true);
                        pre_overhaul.Main.instance.checkBoxSymmetryZ.setSelected(true);
                    }
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
                    int sx = X, sy = Y, sz = Z;
                    pre_overhaul.Fuel sf = fuel;
                    Thread t = new Thread(() -> {
                        while(pre_overhaul.Main.running&&System.nanoTime()<underhaulTime+TIME_LIMIT){
                            try{
                                Thread.sleep(1000);
                                updateUnderhaul("Generating reactors...\n", true);
                            }catch(InterruptedException ex){
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(pre_overhaul.Main.running)pre_overhaul.Main.instance.stop();
                        updateUnderhaul("Generated Reactor", false);
                        File image = new File("underhaul.png");
                        File json = new File("underhaul.json");
                        pre_overhaul.Reactor r = pre_overhaul.Main.genPlan.getReactors().get(0);
                        try{
                            ImageIO.write(r.getImage(new Color(54, 57, 63)), "png", image);
                            message.getChannel().sendFile(image, "Underhaul "+sx+"x"+sy+"x"+sz+" "+sf.toString()+".png").queue();
                            r.exportJSON().write(json);
                            message.getChannel().sendFile(json, "Underhaul "+sx+"x"+sy+"x"+sz+" "+sf.toString()+".json").queue();
                        }catch(Exception ex){
                            message.getChannel().sendMessage(ex.getClass().getName()+": "+ex.getMessage()).queue();
                            ex.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                    for(Attachment at : message.getAttachments()){
                        try{
                            if(at.getFileExtension().equals("json")){
                                String text = "";
                                BufferedReader reader = new BufferedReader(new InputStreamReader(at.retrieveInputStream().get()));
                                String line;
                                while((line = reader.readLine())!=null){
                                    text+=line+"\n";
                                }
                                reader.close();
                                pre_overhaul.Reactor r = pre_overhaul.Reactor.parse(text, fuel, X, Y, Z);
                                if(r==null)throw new NullPointerException("Invalid Reactor");
                                pre_overhaul.Main.genPlan.importReactor(r, true);
                                message.getChannel().sendMessage("Imported reactor: "+at.getFileName()).queue();
                            }else{
                                System.err.println("Unknown extention: "+at.getFileExtension());
                            }
                        }catch(Exception ex){
                            message.getChannel().sendMessage("Failed to parse attachment: "+at.getFileName()+"\n"+ex.getClass().getName()+": "+ex.getMessage()).queue();
                            ex.printStackTrace();
                        }
                    }
                }
            }
            break;
        }
    }
    private void updateOverhaul(String prefix, boolean showLayout){
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
        if(showLayout){
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
        }
        builder.setColor(Color.ORANGE);
        text+="\n"+(r.clusters.size()>1?"*Stability of multi-cluster reactors is not guaranteed*\n":"")+"*Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator*";
        builder.setFooter((r.clusters.size()>1?"Stability of multi-cluster reactors is not guaranteed\n":"")+"Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator");
        if(text.length()>2000)text = text.substring(0, text.indexOf("Reactor Layout"))+"Reactor Layout**\n<Too big>\n"+(r.clusters.size()>1?"*Stability of multi-cluster reactors is not guaranteed*\n":"")+"*Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator*";
        try{
            overhaulFutures.add(overhaulMessage.editMessage(builder.build()).submit());
        }catch(InsufficientPermissionException ex){
            overhaulFutures.add(overhaulMessage.editMessage(text).submit());
        }
    }
    private void updateUnderhaul(String prefix, boolean showLayout){
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
        text+="\n**Details**\n"+details;
        if(showLayout){
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
        }
        builder.setColor(Color.ORANGE);
        builder.setFooter("Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator");
        text+="\n*Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator*";
        if(text.length()>2000)text = text.substring(0, text.indexOf("Reactor Layout"))+"Reactor Layout**\n<Too big>\n*Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator*";
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
    private String getHelp(){
        String prefix = "**Prefixes:**\n";
        for(String pref : prefixes){
            prefix+=pref+"\n";
        }
        if(prefixes.length==1)prefix = "";
        return "__**S'plodo-bot help**__\n"+prefix
                + "**Commands:**\n"
                + prefixes[0]+"help  Shows this help window\n"
                + prefixes[0]+"abort|stop|halt|finish  Stops the currently generating reactor (specify *-abort overhaul* to stop overhaul generation)\n"
                + prefixes[0]+"generate  Generates a reactor with the given parameters\n"
                + "Provide keywords for what type of reactor you wish to generate\n"
                + "*Valid Keywords:*\n"
                + "`overhaul` - generates an overhaul reactor (Default: pre-overhaul)\n"
                + "`XxYxZ` - generates a reactor of size XxYxZ (Default: 3x3x3 for pre-overhaul; 5x5x5 for overhaul)\n"
                + "`<fuel>` - generates a reactor using the specified fuel (Default: LEU-235 Oxide)\n"
                + "`efficiency` or `efficient` - sets efficiency as the main proiority (default)\n"
                + "`output` - sets output as the main priority\n"
                + "`breeder` - sets fuel usage as the main priority (Underhaul only)\n"
                + "`symmetry` or `symmetrical` - applies symmetry to generated reactors\n"
                + "*Examples of valid commands:*\n"
                + prefixes[0]+"generate a 3x3x3 LEU-235 Oxide breeder reactor with symmetry\n"
                + prefixes[0]+"generate an efficient 3x8x3 overhaul reactor using [NI] TBU fuel\n\n"
                + "*Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator*";
    }
}