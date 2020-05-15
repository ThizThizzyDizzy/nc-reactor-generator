package discord;
import common.JSON;
import common.JSON.JSONObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
public class Bot extends ListenerAdapter{
    private static JDA jda;
    private static boolean running = false;
    private static ArrayList<String> prefixes = new ArrayList<>();
    private static final long TIME_LIMIT = 1_000_000_000l*60;//one minute in nanoseconds
    private static long overhaulTime, underhaulTime;
    private int MAX_SIZE = 24; //the biggest allowed value for the X/Y/Z dimensions
    private Message overhaulMessage;
    private Message underhaulMessage;
    private ArrayList<CompletableFuture<Message>> underhaulFutures = new ArrayList<>();
    private ArrayList<CompletableFuture<Message>> overhaulFutures = new ArrayList<>();
    private static String override = "";
    private static ArrayList<Long> botChannels = new ArrayList<>();
    private static ArrayList<Long> dataChannels = new ArrayList<>();
    private static final int batchSize = 100;
    private static final HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<JSONObject>>>> storedReactors =  new HashMap<>();
    private static final HashMap<JSONObject, String> reactorLinks =  new HashMap<>();
    private static int cookies = 0;
    private static Config config;
    static{
        for(int x = 1; x<=24; x++){
            HashMap<Integer, HashMap<Integer, ArrayList<JSONObject>>> xs = new HashMap<>();
            for(int y = 1; y<=24; y++){
                HashMap<Integer, ArrayList<JSONObject>> ys = new HashMap<>();
                for(int z = 1; z<=24; z++){
                    ys.put(z, new ArrayList<>());
                }
                xs.put(y,ys);
            }
            storedReactors.put(x, xs);
        }
    }
    public static void start(String[] args) throws LoginException{
        for(int i = 2; i<args.length; i++){
            String arg = args[i];
            if(arg.startsWith("bot"))botChannels.add(Long.parseLong(arg.substring(3)));
            else if(arg.startsWith("data"))dataChannels.add(Long.parseLong(arg.substring(4)));
            else prefixes.add(arg);
        }
        if(prefixes.isEmpty())prefixes.add("-");
        try{
            Sys.init(File.createTempFile("this", "that").getParentFile(), null);
        }catch(IOException ex){
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
        underhaul.Configuration.load(underhaul.Configuration.DEFAULT);
        overhaul.Configuration.load(overhaul.Configuration.DEFAULT);
        config = Config.newConfig(new File(new File("special.dat").getAbsolutePath()));
        config.load();
        cookies = config.get("cookies", 0);
        JDABuilder b = new JDABuilder(AccountType.BOT);
        b.setToken(args[1]);
        b.setActivity(Activity.watching("cookies accumulate ("+cookies+")"));
        b.addEventListeners(new Bot());
        jda = b.build();
        running = true;
    }
    static void stop(){
        if(running)jda.shutdownNow();
    }
    private final String poweredBy = "Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator/releases";
    @Override
    public void onReady(ReadyEvent re){
        Thread channelRead = new Thread(() -> {
            int bytes = 0;
            int totalCount = 0;
            for(Long id : dataChannels){
                TextChannel channel = jda.getTextChannelById(id);
                if(channel==null){
                    System.err.println("Invalid channel: "+id);
                    continue;
                }
                System.out.println("Reading channel: "+channel.getName());
                MessageHistory history = channel.getHistoryFromBeginning(batchSize).complete();
                int count = 0;
                while(true){
                    Message last = null;
                    ArrayList<Message> messages = new ArrayList<>(history.getRetrievedHistory());
                    Stack<Message> stak = new Stack<>();
                    for(Message m : messages){
                        stak.push(m);
                    }
                    messages.clear();
                    while(!stak.isEmpty())messages.add(stak.pop());
                    for(Message message : messages){
                        last = message;
                        storeReactors(message);
                        for(Attachment att : message.getAttachments()){
                            if(att.getFileExtension().equalsIgnoreCase("json")){
                                count++;
                                System.out.println("Found "+att.getFileName()+" ("+count+")");
                                bytes+=att.getSize();
                            }
                        }
                    }
                    if(last==null)break;
                    if(history.size()<batchSize){
                        break;
                    }else{
                        history = channel.getHistoryAfter(last, batchSize).complete();
                    }
                }
                System.out.println("Finished Reading channel: "+channel.getName()+". Reactors: "+count);
                totalCount+=count;
            }
            System.out.println("Total Reactors: "+totalCount);
            System.out.println("Total Size: "+bytes);
        });
        channelRead.setDaemon(true);
        channelRead.start();
    }
    public void storeReactors(Message message){
        for(Attachment att : message.getAttachments()){
            JSONObject json = storeReactor(att);
            if(json!=null)reactorLinks.put(json, message.getJumpUrl());
        }
    }
    public JSONObject storeReactor(Attachment att){
        if(!att.getFileExtension().equalsIgnoreCase("json"))return null;
        String s = "";
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(att.retrieveInputStream().get()))){
            String line;
            while((line = reader.readLine())!=null){
                s+=line+"\n";
            }
        }catch(IOException|InterruptedException|ExecutionException ex){
            System.err.println("Failed to read file: "+att.getFileName());
            return null;
        }
        int x = 0,y = 0,z = 0;
        JSONObject json = null;
        try{
            json = JSON.parse(s);
            Object dim = json.get("InteriorDimensions");
            if(dim instanceof JSONObject){
                x = ((JSONObject)dim).getInt("X");
                y = ((JSONObject)dim).getInt("Y");
                z = ((JSONObject)dim).getInt("Z");
            }
            if(dim instanceof String){
                String[] strs = ((String)dim).split(",");
                x = Integer.parseInt(strs[0]);
                y = Integer.parseInt(strs[1]);
                z = Integer.parseInt(strs[2]);
            }
        }catch(Exception ex){}
        if(json==null)return null;
        try{
            storedReactors.get(x).get(y).get(z).add(json);
        }catch(Exception ex){}
        return json;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent mre){
        if(mre.getAuthor().isBot())return;
        if(!botChannels.isEmpty()){
            if(!botChannels.contains(mre.getChannel().getIdLong()))return;//not a bot channel! DO NOTHING
        }
        Message message = mre.getMessage();
        storeReactors(message);
        String content = message.getContentStripped();
        for(String prefix : prefixes){
            if(!content.startsWith(prefix))continue;
            content = content.substring(prefix.length());
            content = content.toLowerCase().replace("_", "").replace("-", "").replace(":", "").replace("=", "");
            while(content.contains("  "))content = content.replace("  ", " ");
            content = content.replace(" x ", "x");
            if(content.startsWith("cookie")){
                cookies++;
                config.set("cookies", cookies);
                System.out.println("saving "+cookies+" cookies");
                config.save();
                jda.getPresence().setActivity(Activity.watching("cookies accumulate ("+cookies+")"));
                return;
            }
            if(content.startsWith("help")){
                try{
                    message.getChannel().sendMessage(getHelpEmbed()).queue();
                }catch(InsufficientPermissionException ex){
                    message.getChannel().sendMessage(getHelp()).queue();
                }
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
                    if(underhaul.Main.running){
                        underhaul.Main.instance.stop();
                        message.getChannel().sendMessage("Underhaul generation halted").queue();
                    }else{
                        message.getChannel().sendMessage("Underhaul generator is not running!").queue();
                    }
                }
            }
            if(content.startsWith("generate")||content.startsWith("search")||content.startsWith("find")){
                boolean isGenerating = content.startsWith("generate");
                if(content.contains("overhaul")&&!content.contains("preoverhaul")&&!content.contains("underhaul")){
                    if(overhaul.Main.running){
                        message.getChannel().sendMessage("Overhaul generator is already running!").queue();
                        return;
                    }
                    int X = 5,Y = 5,Z = 5;
                    for(int x = 0; x<=MAX_SIZE; x++){
                        for(int y = 0; y<=MAX_SIZE; y++){
                            for(int z = 0; z<=MAX_SIZE; z++){
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
                    overhaul.Main.genModel = overhaul.GenerationModel.DEFAULT;
                    overhaul.Main.instance = new overhaul.Main();
                    ArrayList<overhaul.ReactorPart> allowedBlocks = new ArrayList<>(overhaul.ReactorPart.parts);
                    allowedBlocks.remove(overhaul.ReactorPart.FUEL_CELL_PO_BE);
                    allowedBlocks.remove(overhaul.ReactorPart.FUEL_CELL_RA_BE);
                    for(overhaul.ReactorPart part : overhaul.ReactorPart.parts){
                        String nam = part.jsonName;
                        if(nam==null)continue;
                        if(content.contains("no"+nam.toLowerCase())||content.contains("no "+nam.toLowerCase())){
                            allowedBlocks.remove(part);
                        }
                    }
                    overhaul.Main.instance.setAllowedBlocks(allowedBlocks);
                    overhaul.Main.instance.checkBoxSymmetryX.setSelected(content.contains("symmetr"));
                    overhaul.Main.instance.checkBoxSymmetryY.setSelected(content.contains("symmetr"));
                    overhaul.Main.instance.checkBoxSymmetryZ.setSelected(content.contains("symmetr"));
                    overhaul.Main.instance.spinnerX.setValue(X);
                    overhaul.Main.instance.spinnerY.setValue(Y);
                    overhaul.Main.instance.spinnerZ.setValue(Z);
                    overhaul.Main.instance.checkBoxFillConductors.setSelected(false);;
                    overhaul.Main.instance.boxFuel.setSelectedIndex(overhaul.Fuel.fuels.indexOf(fuel));
                    overhaul.Main.instance.boxFuelType.setSelectedIndex(type.ordinal());
                    if(!isGenerating){
                        overhaul.Main.genModel = overhaul.GenerationModel.get("None");
                    }
                    overhaul.Main.instance.start();
                    try{
                        overhaulMessage = message.getChannel().sendMessage(new EmbedBuilder().setTitle((isGenerating?"Generating ":"Searching ")+override+"Reactors...").build()).complete();
                    }catch(InsufficientPermissionException ex){
                        overhaulMessage = message.getChannel().sendMessage((isGenerating?"Generating ":"Searching ")+override+"Reactors...").complete();
                    }
                    overhaulTime = System.nanoTime();
                    int sx = X, sy = Y, sz = Z;
                    overhaul.Fuel sf = fuel;
                    overhaul.Fuel.Type sft = type;
                    Thread t = new Thread(() -> {
                        while(overhaul.Main.running&&System.nanoTime()<overhaulTime+TIME_LIMIT){
                            try{
                                Thread.sleep(1000);
                                updateOverhaul((isGenerating?"Generating ":"Searching ")+override+"Reactors...\n", true);
                            }catch(InterruptedException ex){
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(overhaul.Main.running)overhaul.Main.instance.stop();
                        updateOverhaul((isGenerating?"Generated ":"Found ")+override+"Reactor", false);
                        File image = new File("overhaul.png");
                        File json = new File("overhaul.json");
                        overhaul.Reactor r = overhaul.Main.genPlan.getReactors().get(0);
                        try{
                            ImageIO.write(r.getImage(), "png", image);
                            message.getChannel().sendFile(image, "Overhaul "+sx+"x"+sy+"x"+sz+" "+sf.toString()+".png").queue();
                            r.exportJSON().write(json);
                            message.getChannel().sendFile(json, "Overhaul "+sx+"x"+sy+"x"+sz+" "+sf.toString()+" "+sft.toString()+".json").queue();
                        }catch(Exception ex){
                            message.getChannel().sendMessage(ex.getClass().getName()+": "+ex.getMessage()).queue();
                            ex.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                    boolean shouldImport = true;
                    if(isGenerating){
                        for(Attachment at : message.getAttachments()){
                            shouldImport = false;
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
                    }
                    if(shouldImport){
                        System.out.println("Importing "+storedReactors.get(X).get(Y).get(Z).size()+" Reactors...");
                        JSONObject best = null;
                        int imported = 0;
                        for(JSONObject json : storedReactors.get(X).get(Y).get(Z)){
                            imported++;
                            System.out.println("Importing... "+imported+"/ "+storedReactors.get(X).get(Y).get(Z).size());
                            try{
                                overhaul.Reactor r = overhaul.Reactor.parseJSON(json, fuel, type, X, Y, Z);
                                if(r==null){
                                    continue;
                                }
                                final overhaul.Reactor react = r;
                                r = new overhaul.Reactor(fuel, type, X, Y, Z){
                                    @Override
                                    protected overhaul.ReactorPart build(int X, int Y, int Z){
                                        return overhaul.ReactorPart.getSelectedParts().contains(react.parts[X][Y][Z])?react.parts[X][Y][Z]:overhaul.ReactorPart.AIR;
                                    }
                                };
                                boolean isBest = true;
                                for(overhaul.Reactor re : overhaul.Main.genPlan.getReactors()){
                                    if(overhaul.Reactor.isbetter(re, r))isBest = false;
                                }
                                if(isBest&&overhaul.Main.genPlan.importReactor(r, true))best = json;
                                System.out.println("Imported Player-made Reactor!");
                            }catch(Exception ex){}
                        }
                        System.out.println("Done importing reactors");
                        if(best!=null){
                            message.getChannel().sendMessage("Found basis reactor: "+reactorLinks.get(best)).queue();
                        }
                    }
                    if(!isGenerating){
                        overhaul.Main.instance.stop();
                    }
                }else{
                    if(underhaul.Main.running){
                        message.getChannel().sendMessage("Underhaul generator is already running!").queue();
                        return;
                    }
                    if(content.contains("e2e")){
                        override = "E2E ";
                        underhaul.Configuration.load(underhaul.Configuration.E2E);
                    }else if(content.contains("po3")){
                        override = "PO3 ";
                        underhaul.Configuration.load(underhaul.Configuration.PO3);
                    }else{
                        override = "";
                        underhaul.Configuration.load(underhaul.Configuration.DEFAULT);
                    }
                    int X = 3,Y = 3,Z = 3;
                    for(int x = 0; x<=MAX_SIZE; x++){
                        for(int y = 0; y<=MAX_SIZE; y++){
                            for(int z = 0; z<=MAX_SIZE; z++){
                                if(content.contains(x+"x"+y+"x"+z)){
                                    X = x;
                                    Y = y;
                                    Z = z;
                                }
                            }
                        }
                    }
                    underhaul.Fuel fuel = underhaul.Fuel.fuels.get(7);
                    boolean fuelSet = false;
                    for(underhaul.Fuel f : underhaul.Fuel.fuels){
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
                    for(underhaul.Fuel f : underhaul.Fuel.fuels){
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
                        underhaul.Priority.moveToEnd("Output");
                        underhaul.Priority.moveToEnd("Minimize Heat");
                        underhaul.Priority.moveToEnd("Fuel Usage");
                    }else if(content.contains("output")){
                        underhaul.Priority.moveToEnd("Efficiency");
                        underhaul.Priority.moveToEnd("Minimize Heat");
                        underhaul.Priority.moveToEnd("Fuel Usage");
                    }else if(content.contains("breeder")||content.contains("fuel usage")||content.contains("cell count")){
                        underhaul.Priority.moveToEnd("Fuel Usage");
                        underhaul.Priority.moveToEnd("Minimize Heat");
                        underhaul.Priority.moveToEnd("Output");
                        underhaul.Priority.moveToEnd("Efficiency");
                    }else{//default
                        underhaul.Priority.moveToEnd("Output");
                        underhaul.Priority.moveToEnd("Minimize Heat");
                        underhaul.Priority.moveToEnd("Fuel Usage");
                    }
                    if(underhaul.Main.instance!=null)underhaul.Main.instance.dispose();
                    underhaul.Main.genModel = underhaul.GenerationModel.DEFAULT;
                    underhaul.Main.instance = new underhaul.Main();
                    ArrayList<underhaul.ReactorPart> allowedBlocks = new ArrayList<>(underhaul.ReactorPart.parts);
                    allowedBlocks.remove(underhaul.ReactorPart.BERYLLIUM);
                    allowedBlocks.remove(underhaul.ReactorPart.AIR);
                    for(underhaul.ReactorPart part : underhaul.ReactorPart.parts){
                        String nam = part.jsonName;
                        if(nam==null)continue;
                        if(content.contains("no"+nam.toLowerCase())||content.contains("no "+nam.toLowerCase())){
                            allowedBlocks.remove(part);
                        }
                    }
                    underhaul.Main.instance.setAllowedBlocks(allowedBlocks);
                    underhaul.Main.instance.checkBoxSymmetryX.setSelected(content.contains("symmetr"));
                    underhaul.Main.instance.checkBoxSymmetryY.setSelected(content.contains("symmetr"));
                    underhaul.Main.instance.checkBoxSymmetryZ.setSelected(content.contains("symmetr"));
                    underhaul.Main.instance.spinnerX.setValue(X);
                    underhaul.Main.instance.spinnerY.setValue(Y);
                    underhaul.Main.instance.spinnerZ.setValue(Z);
                    underhaul.Main.instance.boxFuel.setSelectedIndex(underhaul.Fuel.fuels.indexOf(fuel));
                    if(!isGenerating){
                        underhaul.Main.genModel = underhaul.GenerationModel.get("None");
                    }
                    underhaul.Main.instance.start();
                    try{
                        underhaulMessage = message.getChannel().sendMessage(new EmbedBuilder().setTitle((isGenerating?"Generating ":"Searching ")+override+"Reactors...").build()).complete();
                    }catch(InsufficientPermissionException ex){
                        underhaulMessage = message.getChannel().sendMessage((isGenerating?"Generating ":"Searching ")+override+"Reactors...").complete();
                    }
                    underhaulTime = System.nanoTime();
                    int sx = X, sy = Y, sz = Z;
                    underhaul.Fuel sf = fuel;
                    Thread t = new Thread(() -> {
                        while(underhaul.Main.running&&System.nanoTime()<underhaulTime+TIME_LIMIT){
                            try{
                                Thread.sleep(1000);
                                updateUnderhaul((isGenerating?"Generating ":"Searching ")+override+"Reactors...\n", true);
                            }catch(InterruptedException ex){
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(underhaul.Main.running)underhaul.Main.instance.stop();
                        updateUnderhaul((isGenerating?"Generated ":"Found ")+override+"Reactor", false);
                        File image = new File("underhaul.png");
                        File json = new File("underhaul.json");
                        underhaul.Reactor r = underhaul.Main.genPlan.getReactors().get(0);
                        try{
                            ImageIO.write(r.getImage(), "png", image);
                            message.getChannel().sendFile(image, (override==null?"Underhaul ":override)+sx+"x"+sy+"x"+sz+" "+sf.toString()+".png").queue();
                            r.exportJSON().write(json);
                            message.getChannel().sendFile(json, (override==null?"Underhaul ":override)+sx+"x"+sy+"x"+sz+" "+sf.toString()+".json").queue();
                        }catch(Exception ex){
                            message.getChannel().sendMessage(ex.getClass().getName()+": "+ex.getMessage()).queue();
                            ex.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                    boolean shouldImport = true;
                    if(isGenerating){
                        for(Attachment at : message.getAttachments()){
                            shouldImport = false;
                            try{
                                if(at.getFileExtension().equals("json")){
                                    String text = "";
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(at.retrieveInputStream().get()));
                                    String line;
                                    while((line = reader.readLine())!=null){
                                        text+=line+"\n";
                                    }
                                    reader.close();
                                    underhaul.Reactor r = underhaul.Reactor.parse(text, fuel, X, Y, Z);
                                    if(r==null)throw new NullPointerException("Invalid Reactor");
                                    underhaul.Main.genPlan.importReactor(r, true);
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
                    if(shouldImport){
                        System.out.println("Importing "+storedReactors.get(X).get(Y).get(Z).size()+" Reactors...");
                        JSONObject best = null;
                        int imported = 0;
                        for(JSONObject json : storedReactors.get(X).get(Y).get(Z)){
                            imported++;
                            System.out.println("Importing... "+imported+"/ "+storedReactors.get(X).get(Y).get(Z).size());
                            try{
                                underhaul.Reactor react = underhaul.Reactor.parseJSON(json, fuel, X, Y, Z);
                                if(react==null){
                                    continue;
                                }
                                underhaul.Reactor r = new underhaul.Reactor(fuel, X, Y, Z){
                                    @Override
                                    protected underhaul.ReactorPart build(int X, int Y, int Z){
                                        return underhaul.ReactorPart.getAvailableParts().contains(react.parts[X][Y][Z])?react.parts[X][Y][Z]:underhaul.ReactorPart.AIR;
                                    }
                                };
                                boolean isBest = true;
                                for(underhaul.Reactor re : underhaul.Main.genPlan.getReactors()){
                                    if(underhaul.Reactor.isbetter(re, r))isBest = false;
                                }
                                if(isBest&&underhaul.Main.genPlan.importReactor(r, true))best = json;
                                System.out.println("Imported Player-made Reactor!");
                            }catch(Exception ex){}
                        }
                        System.out.println("Done importing reactors");
                        if(best!=null){
                            message.getChannel().sendMessage("Found basis reactor: "+reactorLinks.get(best)).queue();
                        }
                    }
                    if(!isGenerating){
                        underhaul.Main.instance.stop();
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
            builder.addField("Reactor Layout", layout.length()>1024?"(Too big for live display)":layout, true);
            text+="\n**Reactor Layout**\n"+layout;
        }
        builder.setColor(Color.ORANGE);
        text+="\n"+(r.clusters.size()>1?"*Stability of multi-cluster reactors is not guaranteed*\n":"")+"*"+poweredBy+"*";
        builder.setFooter((r.clusters.size()>1?"Stability of multi-cluster reactors is not guaranteed\n":"")+poweredBy);
        if(text.length()>2000)text = text.substring(0, text.indexOf("Reactor Layout"))+"Reactor Layout**\n(Too big for live display)\n"+(r.clusters.size()>1?"*Stability of multi-cluster reactors is not guaranteed*\n":"")+"*"+poweredBy+"*";
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
        underhaul.Reactor r = underhaul.Main.genPlan.getReactors().get(0);
        String priorities = "";
        for(underhaul.Priority p : underhaul.Priority.priorities){
            priorities+="\n"+p.toString();
        }
        builder.addField("Priorities", priorities.substring(1), false);
        text+="\n**Priorities**\n"+priorities.substring(1);
        String details = "Size: "+underhaul.Main.instance.spinnerX.getValue()+"x"+underhaul.Main.instance.spinnerY.getValue()+"x"+underhaul.Main.instance.spinnerZ.getValue()+"\n"
                + "Fuel: "+underhaul.Fuel.fuels.get(underhaul.Main.instance.boxFuel.getSelectedIndex())+"\n"+r.getDetails(false);
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
            builder.addField("Reactor Layout", layout.length()>1024?"(Too big for live display)":layout, true);
            text+="\n**Reactor Layout**\n"+layout;
        }
        builder.setColor(Color.ORANGE);
        builder.setFooter(poweredBy);
        text+="\n*"+poweredBy+"*";
        if(text.length()>2000)text = text.substring(0, text.indexOf("Reactor Layout"))+"Reactor Layout**\n(Too big for live display)\n*"+poweredBy+"*";
        try{
            underhaulFutures.add(underhaulMessage.editMessage(builder.build()).submit());
        }catch(InsufficientPermissionException ex){
            underhaulFutures.add(underhaulMessage.editMessage(text).submit());
        }
    }
    private String toEmoteString(underhaul.ReactorPart part, Guild guild){
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
    private String toEString(underhaul.ReactorPart part){
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
        if(prefixes.size()==1)prefix = "";
        return "__**S'plodo-bot help**__\n"+prefix
                + "> **Commands:**\n"
                + "`"+prefixes.get(0)+"help`  Shows this help window\n"
                + "`"+prefixes.get(0)+"abort`|`stop`|`halt`|`finish`  Stops the currently generating reactor (specify `"+prefixes.get(0)+"abort overhaul` to stop overhaul generation)\n"
                + "`"+prefixes.get(0)+"generate`  Generates a reactor with the given parameters\n"
                + "Provide keywords for what type of reactor you wish to generate\n"
                + "**Generation settings:**\n"
                + "`overhaul` - generates an overhaul reactor (Default: underhaul)\n"
                + "`XxYxZ` - generates a reactor of size XxYxZ (Default: 3x3x3 for underhaul; 5x5x5 for overhaul)\n"
                + "`<fuel>` - generates a reactor using the specified fuel (Default: LEU-235 Oxide)\n"
                + "`efficiency` or `efficient` - sets efficiency as the main proiority (default)\n"
                + "`output` - sets output as the main priority\n"
                + "`breeder` or `cell count` or `fuel usage` - sets fuel usage as the main priority (Underhaul only)\n"
                + "`symmetry` or `symmetrical` - applies symmetry to generated reactors\n"
                + "`no <block>` - blacklists a certain block from being used in generation\n"
                + "`e2e` - Uses the Enigmatica 2 Expert config\n"
                + "`po3` - Uses the Project: Ozone 3 config\n"
                + "**Examples of valid commands:**\n"
                + prefixes.get(0)+"generate a 3x3x3 LEU-235 Oxide breeder reactor with symmetry\n"
                + prefixes.get(0)+"generate an efficient 3x8x3 overhaul reactor using [NI] TBU fuel no cryotheum\n\n"
                + "> *"+poweredBy+"*";
    }
    private MessageEmbed getHelpEmbed(){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("S'plodo-bot help");
        if(prefixes.size()>1){
            String prefix = "";
            for(String pref : prefixes){
                prefix+=pref+"\n";
            }
            builder.addField("Prefixes", prefix, false);
        }
        builder.addField("Commands",
                  "`"+prefixes.get(0)+"help`  Shows this help window\n"
                + "`"+prefixes.get(0)+"abort`|`stop`|`halt`|`finish`  Stops the currently generating reactor (specify `"+prefixes.get(0)+"abort overhaul` to stop overhaul generation)\n"
                + "`"+prefixes.get(0)+"generate`  Generates a reactor with the given parameters\n"
                + "`"+prefixes.get(0)+"search`|`find`  Finds a design with the given parameters from <#639859364383031356>\n"
                + "Provide keywords for what type of reactor you wish to generate", false);
        builder.addField("Generation settings",
                  "`overhaul` - generates an overhaul reactor (Default: underhaul)\n"
                + "`XxYxZ` - generates a reactor of size XxYxZ (Default: 3x3x3 for underhaul; 5x5x5 for overhaul)\n"
                + "`<fuel>` - generates a reactor using the specified fuel (Default: LEU-235 Oxide)\n"
                + "`efficiency` or `efficient` - sets efficiency as the main proiority (default)\n"
                + "`output` - sets output as the main priority\n"
                + "`breeder` or `cell count` or `fuel usage` - sets fuel usage as the main priority (Underhaul only)\n"
                + "`symmetry` or `symmetrical` - applies symmetry to generated reactors\n"
                + "`no <block>` - blacklists a certain block from being used in generation\n"
                + "`e2e` - Uses the Enigmatica 2 Expert config\n"
                + "`po3` - Uses the Project: Ozone 3 config",false);
        builder.addField("Examples of valid commands", 
                  prefixes.get(0)+"generate a 3x3x3 LEU-235 Oxide breeder reactor with symmetry\n"
                + prefixes.get(0)+"generate an efficient 3x8x3 overhaul reactor using [NI] TBU fuel no cryotheum",false);
        builder.setFooter(poweredBy);
        builder.setColor(Color.ORANGE);
        return builder.build();
    }
}