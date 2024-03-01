package net.ncplanner.plannerator.discord;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.discord.keyword.KeywordBlockRange;
import net.ncplanner.plannerator.discord.keyword.KeywordConfiguration;
import net.ncplanner.plannerator.discord.keyword.KeywordCube;
import net.ncplanner.plannerator.discord.keyword.KeywordCuboid;
import net.ncplanner.plannerator.discord.keyword.KeywordFormat;
import net.ncplanner.plannerator.discord.keyword.KeywordFuel;
import net.ncplanner.plannerator.discord.keyword.KeywordMultiblock;
import net.ncplanner.plannerator.discord.keyword.KeywordPriority;
import net.ncplanner.plannerator.discord.keyword.KeywordSymmetry;
import net.ncplanner.plannerator.discord.play.Game;
import net.ncplanner.plannerator.discord.play.PlayBot;
import net.ncplanner.plannerator.discord.play.SmoreBot;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.Range;
import net.ncplanner.plannerator.multiblock.configuration.IBlockRecipe;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteGenerator;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.CircularStream;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Core.BufferRenderer;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class Bot extends ListenerAdapter{
    public static boolean debug = false;
    private static ArrayList<String> prefixes = new ArrayList<>();
    private static ArrayList<Long> botChannels = new ArrayList<>();
    private static ArrayList<Long> playChannels = new ArrayList<>();
    private static ArrayList<Long> dataChannels = new ArrayList<>();
    private static Config config;
    private static int cookies;
    private static JDA jda;
    private static final ArrayList<Command> botCommands = new ArrayList<>();
    private static final ArrayList<Command> playCommands = new ArrayList<>();
    private static MenuGenerator generator;
    private static Message generateMessage;
    private static Guild guild = null;
    public static final ArrayList<Project> storedMultiblocks = new ArrayList<>();
    static{
//        botCommands.add(new Command("debug"){
//            @Override
//            public String getHelpText(){
//                return "Toggles Debug Mode";
//            }
//            @Override
//            public void run(User user, MessageChannel channel, String args, boolean debug){
//                Bot.debug = !Bot.debug;
//                channel.sendMessage("Debug mode **"+(Bot.debug?"Enabled":"Disabled")+"**").queue();
//            }
//        });
        botCommands.add(new Command("help"){
            @Override
            public String getHelpText(){
                return "Shows this help window";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                EmbedBuilder builder = createEmbed(jda.getSelfUser().getName()+" Help");
                if(Bot.prefixes.size()>1){
                    String prefx = "";
                    for(String s : Bot.prefixes){
                        prefx+="`"+s+"`\t";
                    }
                    builder.addField("Prefixes", prefx.trim(), false);
                }
                for(Command c : botCommands){
                    if(c.isSecret())continue;
                    String helpText = c.getHelpText();
                    if(helpText.length()>1024){
                        String firstText = helpText.substring(0, 1021)+"...";
                        builder.addField(prefixes.get(0)+c.command, firstText, false);
                        builder.addField(prefixes.get(0)+c.command+" (cont.)", "..."+helpText.substring(1021), false);
                    }else{
                        builder.addField(prefixes.get(0)+c.command, helpText, false);
                    }
                }
                builder.addField("File Formats", "JSON files can be opened in Hellrage's Reactor planner (see #useful-links) or Thiz's planner/generator (see footnote)\n"
                                                +"NCPF files can only be opened in Thiz'z planner/generator (see footnote)", false);
                channel.sendMessage(builder.build()).queue();
            }
        });
        botCommands.add(new Command("stop","abort","halt","cancel","finish"){
            @Override
            public String getHelpText(){
                return "Stops current generation";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(generator!=null)generator.running = false;
                else{
                    channel.sendMessage("Generator is not running!").queue();
                }
            }
        });
        botCommands.add(new KeywordCommand("generate", "gen"){
            @Override
            public String getHelpText(){
                return "**Generation settings**\n" +
"overhaul - generates an overhaul reactor (Default: underhaul)\n" +
"XxYxZ - generates a reactor of size XxYxZ (Default: 3x3x3)\n" +
"<fuel> - generates a reactor using the specified fuel (Default: TBU)\n" +
"efficiency or efficient - sets efficiency as the main proiority (default)\n" +
"output - sets output as the main priority\n" +
"breeder or cell count or fuel usage - sets fuel usage as the main priority (Underhaul)\n" +
"irradiator - sets irradiation as the main priority (Overhaul)\n" +
"symmetry or symmetrical - applies symmetry to generated reactors\n" +
"no <block> - blacklists a certain block from being used in generation\n" +
"e2e - Uses the Enigmatica 2 Expert config\n" +
"po3 - Uses the Project: Ozone 3 config\r\n" +
"**Special Fuels**\n" +
"Yellorium (Extreme Reactors)\n" +
"IC2-MOX (IC2)\n" +
"Enriched Uranium (IC2)\n" +
"Uranium Ingot (E2E Only)\r\n" +
"**Other**\n" +
"For active cooling (Underhaul), add a keyword such as `0-4 active water` to allow up to 4 active water coolers\r\n" +
"**Examples of valid commands**\n" +
"-generate a 3x3x3 PO3 LEU-235 Oxide breeder with symmetry\n" +
"-generate an efficient 3x8x3 overhaul reactor using [NI] TBU fuel no cryotheum";
            }
            @Override
            public void run(User user, MessageChannel channel, ArrayList<Keyword> keywords, boolean debug){
                if(generator!=null){
                    channel.sendMessage("Generator is already running!\nUse `"+prefixes.get(0)+"stop` to stop generation").queue();
                    return;
                }
                KeywordMultiblock multiblockKeyword = null;
                Configuration configuration = null;
                ArrayList<Range<String>> stringRanges = new ArrayList<>();
                ArrayList<String> fuelStrings = new ArrayList<>();
                ArrayList<String> priorityStrings = new ArrayList<>();
                ArrayList<String> symmetryStrings = new ArrayList<>();
                ArrayList<String> formatStrings = new ArrayList<>();
                int x = 0, y = 0, z = 0;
                //<editor-fold defaultstate="collapsed" desc="Keyword Scanning">
                for(Keyword keyword : keywords){
                    if(keyword instanceof KeywordConfiguration){
                        if(configuration!=null){
                            channel.sendMessage("Please choose no more than one configuration!").queue();
                            return;
                        }
                        configuration = ((KeywordConfiguration)keyword).config;
                    }else if(keyword instanceof KeywordMultiblock){
                        if(multiblockKeyword!=null){
                            channel.sendMessage("Please choose no more than one multiblock type!").queue();
                            return;
                        }
                        multiblockKeyword = (KeywordMultiblock)keyword;
                    }else if(keyword instanceof KeywordCube){
                        KeywordCube cube = (KeywordCube)keyword;
                        if(x==0&&y==0&&z==0){
                            x = y = z = cube.size;
                        }else{
                            channel.sendMessage("You may only choose one size!").queue();
                            return;
                        }
                    }else if(keyword instanceof KeywordCuboid){
                        KeywordCuboid cuboid = (KeywordCuboid)keyword;
                        if(x==0&&y==0&&z==0){
                            x = cuboid.x;
                            y = cuboid.y;
                            z = cuboid.z;
                        }else{
                            channel.sendMessage("You may only choose one size!").queue();
                            return;
                        }
                    }else if(keyword instanceof KeywordBlockRange){
                        KeywordBlockRange range = (KeywordBlockRange)keyword;
                        stringRanges.add(new Range(range.block, range.min, range.max));
                    }else if(keyword instanceof KeywordFuel){
                        fuelStrings.add(((KeywordFuel)keyword).fuel);
                    }else if(keyword instanceof KeywordPriority){
                        priorityStrings.add(((KeywordPriority)keyword).input);
                    }else if(keyword instanceof KeywordSymmetry){
                        symmetryStrings.add(((KeywordSymmetry)keyword).input);
                    }else if(keyword instanceof KeywordFormat){
                        formatStrings.add(((KeywordFormat)keyword).input);
                    }
                }
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Validation">
                if(x==0||y==0||z==0){
                    x = y = z = 3;
                }
                if(configuration==null)configuration = Configuration.configurations.get(0);
                Core.setConfiguration(configuration);
                Multiblock multiblock = null;
                if(multiblockKeyword==null){
                    multiblockKeyword = new KeywordMultiblock();
                    multiblockKeyword.read("SFR");
                }
                Multiblock template = multiblockKeyword.getMultiblock();
                if(template==null){
                    channel.sendMessage("Unknown multiblock: `"+multiblockKeyword.text.toUpperCase(Locale.ROOT)+"`!").queue();
                    return;
                }
                multiblock = template.newInstance(Core.project.conglomeration);
                ArrayList<Range<AbstractBlock>> blockRanges = new ArrayList<>();
                if(multiblock instanceof CuboidalMultiblock){
                    CuboidalMultiblock cm = (CuboidalMultiblock)multiblock;
                    if(x<cm.getMinX()||y<cm.getMinY()||z<cm.getMinZ()){
                        channel.sendMessage("Too small! Minimum size: "+cm.getMinX()+"x"+cm.getMinY()+"x"+cm.getMinZ()).queue();
                        return;
                    }
                    if(x>cm.getMaxX()||y>cm.getMaxY()||z>cm.getMaxZ()){
                        channel.sendMessage("Too big! Maximum size: "+cm.getMaxX()+"x"+cm.getMaxY()+"x"+cm.getMaxZ()).queue();
                        return;
                    }
                }
                ArrayList<AbstractBlock> availableBlocks = new ArrayList<>();
                multiblock.getAvailableBlocks(availableBlocks);
                FOR:for(Range<String> range : stringRanges){
                    for(AbstractBlock block : availableBlocks){
                        if(block.roughMatch(range.obj)){
                            blockRanges.add(new Range(block, range.min, range.max));
                            continue FOR;
                        }
                    }
                    channel.sendMessage("Unknown block: `"+range.obj+"`!").queue();
                    return;
                }
                HashMap<Integer, NCPFElement> theMultiblockRecipes = new HashMap<>();
                List<NCPFElement>[] multiblockRecipes = multiblock.getSpecificConfiguration().getMultiblockRecipes();
                for(int i = 0; i<multiblockRecipes.length; i++){
                    NCPFElement recipe = null;
                    RECIPE:for(String str : fuelStrings){
                        for(NCPFElement r : multiblockRecipes[i]){
                            for(String nam : r.getLegacyNames()){
                                if(nam.equalsIgnoreCase(str)){
                                    if(recipe!=null){
                                        channel.sendMessage("You can only pick one multiblock recipe!").queue();
                                        return;
                                    }
                                    recipe = r;
                                    continue RECIPE;
                                }
                            }
                        }
                        channel.sendMessage("Unknown multiblock recipe: "+str).queue();
                        return;
                    }
                    if(recipe==null)recipe = multiblockRecipes[i].get(0);
                    theMultiblockRecipes.put(i, recipe);
                }
                ArrayList<NCPFElement> blockRecipes = new ArrayList<>();
                NCPFElement defaultRecipe = null;
                for(AbstractBlock b : availableBlocks){
                    if(b.hasRecipes()&&defaultRecipe==null)defaultRecipe = (NCPFElement)b.getRecipes().get(0);
                }
                RECIPE:for(String str : fuelStrings){
                    for(AbstractBlock b : availableBlocks){
                        if(!b.hasRecipes())continue;
                        for(IBlockRecipe recip : b.getRecipes()){
                            NCPFElement recipe = (NCPFElement)recip;
                            if(recip.getDisplayName().equalsIgnoreCase(str)){
                                blockRecipes.add(recipe);
                                continue RECIPE;
                            }
                            for(String s : recipe.getLegacyNames()){
                                if(s.equalsIgnoreCase(str)){
                                    blockRecipes.add(recipe);
                                    continue RECIPE;
                                }
                            }
                        }
                    }
                    channel.sendMessage("Unknown block recipe: "+str).queue();
                    return;
                }
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Calculations and stuff">
                if(!symmetryStrings.isEmpty())channel.sendMessage("TODO symmetry configuration").queue();//TODO symmetry configuration
                ArrayList<FormatWriter> formats = new ArrayList<>();
                for(String format : formatStrings){
                    for(FormatWriter writer : FileWriter.formats){
                        for(String extention : writer.getFileFormat().extensions){
                            if(format.toLowerCase(Locale.ROOT).contains(extention)){
                                formats.add(writer);
                                break;
                            }
                        }
                    }
                }
                if(formats.isEmpty()){
                    if(multiblock.getDefinitionName().contains("Underhaul"))formats.add(FileWriter.HELLRAGE);
                    formats.add(FileWriter.LEGACY_NCPF);
                    formats.add(FileWriter.NCPF);
                }
                formats.add(FileWriter.PNG);
                Multiblock multiblockInstance = multiblock.newInstance(Core.project.conglomeration,x,y,z);
                for(int i : theMultiblockRecipes.keySet()){
                    multiblockInstance.setMultiblockRecipe(i, theMultiblockRecipes.get(i));
                }
                if(!blockRecipes.isEmpty())channel.sendMessage("TODO block recipes filtering").queue();//TODO block recipes filtering
//</editor-fold>
                generator = Core.gui.open(new MenuGenerator(Core.gui, null, multiblock));
                LiteGenerator gen = null;
                for(String str : priorityStrings){
                    for(LiteGenerator g : generator.gens){
                        if(g.name.get().equalsIgnoreCase(str)){
                            if(gen!=null){
                                channel.sendMessage("You can only target one priority at a time!\nDownload the generator for more control over generation priorities (see footnote)").queue();
                                return;
                            }
                            gen = g;
                        }
                    }
                }
                if(gen!=null)generator.generator = gen;
                if(generator.generator==null)throw new IllegalArgumentException("No generators available for multiblock!");
                if(!blockRanges.isEmpty())channel.sendMessage("TODO block ranges").queue();//TODO block ranges
                //<editor-fold defaultstate="collapsed" desc="Generation">
                Thread t = new Thread(() -> {
                    generator.start();
                    synchronized(storedMultiblocks){
                        IMPORT:for(Project file : storedMultiblocks){
                            for(Design d : file.designs){
                                if(d instanceof MultiblockDesign){
                                    Multiblock m = ((MultiblockDesign)d).toMultiblock();
                                    if(m.getDefinitionName().equals(multiblockInstance.getDefinitionName())){
                                        channel.sendMessage("TODO import multiblocks");//TODO import multiblocks
                                        break IMPORT;
                                    }
                                }
                            }
                        }
                    }
                    String configName = Core.project.getConfigName();
                    generateMessage = channel.sendMessage(createEmbed("Generating "+(configName==null?"":configName+" ")+multiblockInstance.getGeneralName()+"s...").addField(multiblockInstance.getGeneralName(), generator.multiblock.getTooltip(), false).build()).complete();
                    int time = 0;
                    int interval = 1500;//1.5 sec
                    int timeout = 300000;//5 minutes
                    while(true){
                        try{
                            Thread.sleep(interval);
                        }catch(InterruptedException ex){
                            printErrorMessage(channel, "Generation Interrupted!", ex);
                            break;
                        }
                        time+=interval;
                        if(!generator.running)break;
                        generateMessage.editMessage(createEmbed("Generating "+(configName==null?"":configName+" ")+multiblockInstance.getGeneralName()+"s...").addField(multiblockInstance.getGeneralName(), generator.multiblock.getTooltip(), false).build()).queue();
                        if(time>timeout)break;
                    }
                    generator.running = false;
                    Multiblock finalMultiblock = generator.multiblock.export(Core.project.conglomeration);
                    if(finalMultiblock==null||finalMultiblock.isEmpty()){
                        generateMessage.editMessage(createEmbed("No "+multiblockInstance.getGeneralName().toLowerCase(Locale.ROOT)+" was generated. :(").build()).queue();
                    }else{
                        generateMessage.editMessage(createEmbed("Generated "+(configName==null?"":configName+" ")+multiblockInstance.getGeneralName()).addField(multiblockInstance.getGeneralName(), finalMultiblock.getBotTooltip(), false).build()).queue();
                        Project ncpf = new Project();
                        String name = UUID.randomUUID().toString();
                        ncpf.metadata.put("Author", "S'plodo-Bot");
                        ncpf.metadata.put("Name", name);
                        finalMultiblock.metadata.put("Author", "S'plodo-Bot");
                        finalMultiblock.metadata.put("Name", name);
                        GregorianCalendar calendar = new GregorianCalendar();
                        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                        ncpf.metadata.put("Generation Date", months[calendar.get(Calendar.MONTH)]+" "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR));
                        ncpf.metadata.put("Generation Time", calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+"."+calendar.get(Calendar.MILLISECOND));
                        ncpf.designs.add(finalMultiblock.toDesign());
                        ncpf.configuration = ncpf.conglomeration = Core.project.conglomeration;
                        for(FormatWriter writer : formats){
                            if(writer.isMultiblockSupported(finalMultiblock)){
                                CircularStream stream = new CircularStream(1024*1024);//1MB
                                CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), (configName==null?"":configName+" ")+multiblockInstance.getDimensionsStr()+" "+multiblockInstance.getGeneralName()+"."+writer.getFileFormat().extensions[0]).submit();
                                try{
                                    writer.write(ncpf, stream);
                                }catch(Exception ex){
                                    printErrorMessage(channel, "Failed to write file", ex);
                                    submit.cancel(true);
                                    stream.close();
                                }
                            }
                        }
                    }
                    generator = null;
                    generateMessage = null;
                });
                t.setDaemon(true);
                t.setName("Discord Bot Generation Thread");
                t.start();
//</editor-fold>
            }
            @Override
            public void addKeywords(){
                addKeyword(new KeywordCuboid());
                addKeyword(new KeywordCube());
                addKeyword(new KeywordSymmetry());
                addKeyword(new KeywordConfiguration());
                addKeyword(new KeywordFormat());
                addKeyword(new KeywordPriority());
                addKeyword(new KeywordMultiblock());
                addKeyword(new KeywordFuel());
                addKeyword(new KeywordBlockRange());
            }
        });
        /*
        botCommands.add(new KeywordCommand("find", "search"){
            @Override
            public String getHelpText(){
                return "Searches for a stored reactor that matches the given specifics. Uses same syntax as -generate";
            }
            @Override
            public void run(User user, MessageChannel channel, ArrayList<Keyword> keywords, boolean debug){
                boolean underhaul = false;
                boolean overhaul = false;
                KeywordMultiblock multiblockKeyword = null;
                Configuration configuration = null;
                ArrayList<Range<String>> stringRanges = new ArrayList<>();
                ArrayList<String> fuelStrings = new ArrayList<>();
                ArrayList<String> priorityStrings = new ArrayList<>();
                ArrayList<String> symmetryStrings = new ArrayList<>();
                ArrayList<String> formatStrings = new ArrayList<>();
                int x = 0, y = 0, z = 0;
                //<editor-fold defaultstate="collapsed" desc="Keyword Scanning">
                for(Keyword keyword : keywords){
                    if(keyword instanceof KeywordOverhaul){
                        overhaul = true;
                    }else if(keyword instanceof KeywordUnderhaul){
                        underhaul = true;
                    }else if(keyword instanceof KeywordConfiguration){
                        if(configuration!=null){
                            channel.sendMessage("Please choose no more than one configuration!").queue();
                            return;
                        }
                        configuration = ((KeywordConfiguration)keyword).config;
                    }else if(keyword instanceof KeywordMultiblock){
                        if(multiblockKeyword!=null){
                            channel.sendMessage("Please choose no more than one multiblock type!").queue();
                            return;
                        }
                        multiblockKeyword = (KeywordMultiblock)keyword;
                    }else if(keyword instanceof KeywordCube){
                        KeywordCube cube = (KeywordCube)keyword;
                        if(x==0&&y==0&&z==0){
                            x = y = z = cube.size;
                        }else{
                            channel.sendMessage("You may only choose one size!").queue();
                            return;
                        }
                    }else if(keyword instanceof KeywordCuboid){
                        KeywordCuboid cuboid = (KeywordCuboid)keyword;
                        if(x==0&&y==0&&z==0){
                            x = cuboid.x;
                            y = cuboid.y;
                            z = cuboid.z;
                        }else{
                            channel.sendMessage("You may only choose one size!").queue();
                            return;
                        }
                    }else if(keyword instanceof KeywordBlockRange){
                        KeywordBlockRange range = (KeywordBlockRange)keyword;
                        stringRanges.add(new Range(range.block, range.min, range.max));
                    }else if(keyword instanceof KeywordFuel){
                        fuelStrings.add(((KeywordFuel)keyword).fuel);
                    }else if(keyword instanceof KeywordPriority){
                        priorityStrings.add(((KeywordPriority)keyword).input);
                    }else if(keyword instanceof KeywordSymmetry){
                        symmetryStrings.add(((KeywordSymmetry)keyword).input);
                    }else if(keyword instanceof KeywordFormat){
                        formatStrings.add(((KeywordFormat)keyword).input);
                    }
                }
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Validation">
                if(!(underhaul||overhaul))underhaul = true;
                if(configuration==null)configuration = Configuration.configurations.get(0);
                if(underhaul&&overhaul){
                    channel.sendMessage("Please choose either `underhaul` or `overhaul`, not both!").queue();
                    return;
                }
                if(configuration.underhaul==null&&underhaul){
                    channel.sendMessage("`"+configuration.name+" doesn't have an Underhaul configuration!").queue();
                    return;
                }
                if(configuration.overhaul==null&&overhaul){
                    channel.sendMessage("`"+configuration.name+" doesn't have an Overhaul configuration!").queue();
                    return;
                }
                Multiblock multiblock = null;
                if(multiblockKeyword==null){
                    multiblockKeyword = new KeywordMultiblock();
                    multiblockKeyword.read("SFR");
                }
                Multiblock template = multiblockKeyword.getMultiblock(overhaul);
                if(template==null){
                    channel.sendMessage("Unknown multiblock: `"+(overhaul?"Overhaul ":"Underhaul ")+multiblockKeyword.text.toUpperCase(Locale.ROOT)+"`!").queue();
                    return;
                }
                multiblock = template.newInstance(configuration);
                ArrayList<Range<AbstractBlock>> blockRanges = new ArrayList<>();
                if(multiblock.getDefinitionName().contains("Turbine"))z+=2;
                if(x==0&&y==0&&z==0){
                    //this is fine.
                }else{
                    if(multiblock instanceof CuboidalMultiblock){
                        CuboidalMultiblock cm = (CuboidalMultiblock)multiblock;
                        if(x<cm.getMinX()||y<cm.getMinY()||z<cm.getMinZ()){
                            channel.sendMessage("Too small! Minimum size: "+cm.getMinX()+"x"+cm.getMinY()+"x"+cm.getMinZ()).queue();
                            return;
                        }
                        if(x>cm.getMaxX()||y>cm.getMaxY()||z>cm.getMaxZ()){
                            channel.sendMessage("Too big! Maximum size: "+cm.getMaxX()+"x"+cm.getMaxY()+"x"+cm.getMaxZ()).queue();
                            return;
                        }
                    }
                }
                ArrayList<AbstractBlock> availableBlocks = new ArrayList<>();
                multiblock.getAvailableBlocks(availableBlocks);
                FOR:for(Range<String> range : stringRanges){
                    for(AbstractBlock block : availableBlocks){
                        if(block.roughMatch(range.obj)){
                            blockRanges.add(new Range(block, range.min, range.max));
                            continue FOR;
                        }
                    }
                    channel.sendMessage("Unknown block: `"+range.obj+"`!").queue();
                    return;
                }
                Object fuels = null;
                ArrayList blockRecipes = null;
                if(multiblock instanceof UnderhaulSFR){
                    ArrayList<net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel> underFuels = new ArrayList<>();
                    FUEL:for(String str : fuelStrings){
                        for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f : configuration.underhaul.fissionSFR.allFuels){
                            for(String nam : f.getLegacyNames()){
                                if(nam.equalsIgnoreCase(str)){
                                    underFuels.add(f);
                                    continue FUEL;
                                }
                            }
                        }
                        channel.sendMessage("Unknown fuel: "+str).queue();
                        return;
                    }
                    fuels = underFuels.isEmpty()?null:underFuels;
                }
                if(multiblock instanceof OverhaulSFR){
                    ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe> sfrRecipes = new ArrayList<>();
                    FUEL:for(String str : fuelStrings){
                        for(AbstractBlock b : availableBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : ((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)b).template.allRecipes){
                                if(recipe.getInputDisplayName().equalsIgnoreCase(str)){
                                    sfrRecipes.add(recipe);
                                    continue FUEL;
                                }
                                for(String s : recipe.getLegacyNames()){
                                    if(s.equalsIgnoreCase(str)){
                                        sfrRecipes.add(recipe);
                                        continue FUEL;
                                    }
                                }
                            }
                        }
                        channel.sendMessage("Unknown fuel: "+str).queue();
                        return;
                    }
                    blockRecipes = sfrRecipes.isEmpty()?null:sfrRecipes;
                }
                if(multiblock instanceof OverhaulMSR){
                    ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe> msrRecipes = new ArrayList<>();
                    FUEL:for(String str : fuelStrings){
                        for(AbstractBlock b : availableBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : ((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)b).template.allRecipes){
                                if(recipe.getInputDisplayName().equalsIgnoreCase(str)){
                                    msrRecipes.add(recipe);
                                    continue FUEL;
                                }
                                for(String s : recipe.getLegacyNames()){
                                    if(s.equalsIgnoreCase(str)){
                                        msrRecipes.add(recipe);
                                        continue FUEL;
                                    }
                                }
                            }
                        }
                        channel.sendMessage("Unknown fuel: "+str).queue();
                        return;
                    }
                    blockRecipes = msrRecipes.isEmpty()?null:msrRecipes;
                }
                Priority.Preset priority = null;
                ArrayList<Priority> priorities = multiblock.getGenerationPriorities();
                ArrayList<Priority.Preset> presets = multiblock.getGenerationPriorityPresets(priorities);
                for(Priority.Preset preset : presets){
                    for(String str : priorityStrings){
                        for(String alternative : (ArrayList<String>)preset.alternatives){
                            if(str.equalsIgnoreCase(alternative)){
                                if(priority!=null){
                                    channel.sendMessage("You can only target one priority at a time!").queue();
                                    return;
                                }
                                priority = preset;
                            }
                        }
                    }
                }
                if(priority==null)priority = presets.get(0);
                priority.set(priorities);
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="pre-calculations">
                ArrayList<Symmetry> symmetries = new ArrayList<>();
                ArrayList<Symmetry> availableSymmetries = multiblock.getSymmetries();
                for(Symmetry symmetry : availableSymmetries){
                    for(String sym : symmetryStrings){
                        if(symmetry instanceof AxialSymmetry){
                            if(((AxialSymmetry)symmetry).matches(sym)){
                                symmetries.add(symmetry);
                            }
                        }else if(symmetry instanceof CoilSymmetry){
                            if(((CoilSymmetry)symmetry).matches(sym)){
                                symmetries.add(symmetry);
                            }
                        }else{
                            if(symmetry.name.equalsIgnoreCase(sym))symmetries.add(symmetry);
                        }
                    }
                }
                ArrayList<FormatWriter> formats = new ArrayList<>();
                for(String format : formatStrings){
                    for(FormatWriter writer : FileWriter.formats){
                        for(String extention : writer.getFileFormat().extensions){
                            if(format.toLowerCase(Locale.ROOT).contains(extention)){
                                formats.add(writer);
                                break;
                            }
                        }
                    }
                }
                if(formats.isEmpty()){
                    formats.add(FileWriter.HELLRAGE);
                    formats.add(FileWriter.LEGACY_NCPF);
                }
                formats.add(FileWriter.PNG);
//</editor-fold>
                final Multiblock mb = multiblock;
                final Configuration cfg = configuration;
                final int X = x;
                final int Y = y;
                final int Z = z;
                final Object feuls = fuels;
                //<editor-fold defaultstate="collapsed" desc="Searching">
                Thread t = new Thread(() -> {
                    String configName = cfg.getShortName();
                    Multiblock finalMultiblock = null;
                    int total = 0;
                    synchronized(storedMultiblocks){
                        for(LegacyNCPFFile ncpf : storedMultiblocks){
                            MULTIBLOCK:for(Multiblock m : ncpf.multiblocks){
                                if(ncpf.configuration==null){
                                    m = m.copy();
                                    try{
                                        m.convertTo(cfg);
                                    }catch(Exception ex){
                                        continue;
                                    }
                                }
                                if(!m.getConfiguration().nameMatches(cfg))continue;//wrong configuration
                                m = m.copy();
                                try{
                                    m.convertTo(cfg);
                                }catch(Exception ex){
                                    continue;
                                }
                                if(!m.getDefinitionName().equals(mb.getDefinitionName()))continue;//wrong multiblock type
                                if(X!=0&&Y!=0&&Z!=0){
                                    if(m instanceof CuboidalMultiblock){
                                        CuboidalMultiblock cm = (CuboidalMultiblock)m;
                                        if(cm.getInternalWidth()!=X||cm.getInternalHeight()!=Y||cm.getInternalDepth()!=Z)continue;//wrong size
                                    }
                                }
                                for(Symmetry sym : symmetries){
                                    if(!sym.check(m))continue MULTIBLOCK;//symmetry doesn't match
                                }
                                if(feuls!=null){
                                    if(m instanceof UnderhaulSFR){
                                        boolean yay = false;
                                        for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f : (ArrayList<net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel>)feuls){
                                            if(((UnderhaulSFR)m).fuel.equals(f))yay = true;
                                        }
                                        if(!yay)continue;
                                    }
                                    if(m instanceof OverhaulSFR){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe fuel : ((OverhaulSFR)m).getRecipeCounts().keySet()){
                                            boolean yay = false;
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe f : (ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe>)feuls){
                                                if(fuel.equals(f))yay = true;
                                            }
                                            if(!yay)continue MULTIBLOCK;
                                        }
                                    }
                                    if(m instanceof OverhaulMSR){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe fuel : ((OverhaulMSR)m).getRecipeCounts().keySet()){
                                            boolean yay = false;
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe f : (ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe>)feuls){
                                                if(fuel.equals(f))yay = true;
                                            }
                                            if(!yay)continue MULTIBLOCK;
                                        }
                                    }
                                }
                                for(Range<AbstractBlock> range : blockRanges){
                                    int count = m.count(range.obj);
                                    if(count<range.min)continue MULTIBLOCK;
                                    if(count>range.max)continue MULTIBLOCK;
                                }
                                m.recalculate();
                                if(finalMultiblock==null||m.isBetterThan(finalMultiblock, priorities))finalMultiblock = m;
                            }
                        }
                    }
                    if(finalMultiblock==null||finalMultiblock.isEmpty()){
                        channel.sendMessage(createEmbed("No "+mb.getGeneralName().toLowerCase(Locale.ROOT)+" was found. try `-generate` to make a new "+mb.getGeneralName().toLowerCase(Locale.ROOT)+".").build()).queue();
                    }else{
                        LegacyNCPFFile ncpf = new LegacyNCPFFile();
                        ncpf.multiblocks.add(finalMultiblock);
                        ncpf.configuration = PartialConfiguration.generate(finalMultiblock.getConfiguration(), ncpf.multiblocks);
                        channel.sendMessage(createEmbed("Found "+(configName==null?"":configName+" ")+mb.getGeneralName()).addField(mb.getGeneralName(), finalMultiblock.getBotTooltip(), false).build()).queue();
                        for(FormatWriter writer : formats){
                            if(writer.isMultiblockSupported(finalMultiblock)){
                                CircularStream stream = new CircularStream(1024*1024);//1MB
                                CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), (configName==null?"":configName+" ")+finalMultiblock.getDimensionsStr()+" "+mb.getGeneralName()+"."+writer.getFileFormat().extensions[0]).submit();
                                try{
                                    writer.write(ncpf, stream);
                                }catch(Exception ex){
                                    printErrorMessage(channel, "Failed to write file", ex);
                                    submit.cancel(true);
                                    stream.close();
                                }
                            }
                        }
                    }
                });
                t.setDaemon(true);
                t.setName("Discord Bot Search Thread");
                t.start();
//</editor-fold>
            }
            @Override
            public void addKeywords(){
                addKeyword(new KeywordCuboid());
                addKeyword(new KeywordCube());
                addKeyword(new KeywordUnderhaul());
                addKeyword(new KeywordOverhaul());
                addKeyword(new KeywordSymmetry());
                addKeyword(new KeywordConfiguration());
                addKeyword(new KeywordFormat());
                addKeyword(new KeywordPriority());
                addKeyword(new KeywordMultiblock());
                addKeyword(new KeywordFuel());
                addKeyword(new KeywordBlockRange());
            }
        });
        //game commands
        playCommands.add(new KeywordCommand("hangman"){
            @Override
            public void addKeywords(){
                addKeyword(new KeywordUnderhaul());
                addKeyword(new KeywordOverhaul());
                addKeyword(new KeywordConfiguration());
                addKeyword(new KeywordMultiblock());
                addKeyword(new KeywordBlind());
            }
            @Override
            public void run(User user, MessageChannel channel, ArrayList<Keyword> keywords, boolean debug){
                boolean underhaul = false;
                boolean overhaul = false;
                boolean blind = false;
                ArrayList<KeywordMultiblock> multiKeywords = new ArrayList<>();
                for(Keyword k :keywords){
                    if(k instanceof KeywordUnderhaul)underhaul = true;
                    if(k instanceof KeywordOverhaul)overhaul = true;
                    if(k instanceof KeywordMultiblock)multiKeywords.add((KeywordMultiblock)k);
                    if(k instanceof KeywordBlind)blind = true;
                }
                if(!underhaul&&!overhaul){
                    underhaul = overhaul = true;
                }
                Set<Multiblock> allowedMultiblocks = new HashSet<>();
                for(KeywordMultiblock mk : multiKeywords){
                    if(overhaul==true)allowedMultiblocks.add(mk.getMultiblock(true));
                    if(underhaul==true)allowedMultiblocks.add(mk.getMultiblock(false));
                }
                allowedMultiblocks.remove(null);
                if(allowedMultiblocks.isEmpty()){
                    for(Multiblock m : Core.multiblockTypes){
                        if(m.getDefinitionName().contains("Overhaul")&&overhaul)allowedMultiblocks.add(m);
                        if(m.getDefinitionName().contains("Underhaul")&&underhaul)allowedMultiblocks.add(m);
                    }
                }
                PlayBot.play(channel, new Hangman(blind, new ArrayList<>(allowedMultiblocks)));
            }
            @Override
            public String getHelpText(){
                return "Play some Hangman";
            }
//            @Override
//            public String getHelpText(){
//                return "Play some Hangman";
//            }
//            @Override
//            public void run(User user, MessageChannel channel, String args, boolean debug){
//                PlayBot.play(channel, new Hangman(args.trim().equalsIgnoreCase("blind")));
//            }
        });
        playCommands.add(new KeywordCommand("battle", "heatsinkbattle", "coolingbattle", "heatingbattle", "heaterbattle", "coolerbattle", "heatbattle"){
            @Override
            public void addKeywords(){
                addKeyword(new KeywordUnderhaul());
                addKeyword(new KeywordOverhaul());
                addKeyword(new KeywordConfiguration());
                addKeyword(new KeywordMultiblock());
                addKeyword(new KeywordSmores());
            }
            @Override
            public void run(User user, MessageChannel channel, ArrayList<Keyword> keywords, boolean debug){
                Game game = PlayBot.games.get(channel.getIdLong());
                if(game instanceof HeatsinkBattle){
                    ((HeatsinkBattle)game).addPlayer(user);
                }else{
                    boolean underhaul = false;
                    boolean overhaul = false;
                    ArrayList<KeywordMultiblock> multiKeywords = new ArrayList<>();
                    int smores = 0;
                    for(Keyword k :keywords){
                        if(k instanceof KeywordUnderhaul)underhaul = true;
                        if(k instanceof KeywordOverhaul)overhaul = true;
                        if(k instanceof KeywordMultiblock)multiKeywords.add((KeywordMultiblock)k);
                        if(k instanceof KeywordSmores)smores = ((KeywordSmores)k).numSmores;
                    }
                    if(!underhaul&&!overhaul){
                        underhaul = overhaul = true;
                    }
                    Set<Multiblock> allowedMultiblocks = new HashSet<>();
                    for(KeywordMultiblock mk : multiKeywords){
                        if(overhaul==true)allowedMultiblocks.add(mk.getMultiblock(true));
                        if(underhaul==true)allowedMultiblocks.add(mk.getMultiblock(false));
                    }
                    allowedMultiblocks.remove(null);
                    if(allowedMultiblocks.isEmpty()){
                        for(Multiblock m : Core.multiblockTypes){
                            if(m.getDefinitionName().contains("Overhaul")&&overhaul)allowedMultiblocks.add(m);
                            if(m.getDefinitionName().contains("Underhaul")&&underhaul)allowedMultiblocks.add(m);
                        }
                    }
                    HeatsinkBattle battle = new HeatsinkBattle(new ArrayList<>(allowedMultiblocks)).setSmores(smores);
                    PlayBot.play(channel, battle);
                    battle.addPlayer(user);
                }
            }
            @Override
            public String getHelpText(){
                return "Play some Hangman";
            }
//            @Override
//            public String getHelpText(){
//                return "Play some Hangman";
//            }
//            @Override
//            public void run(User user, MessageChannel channel, String args, boolean debug){
//                PlayBot.play(channel, new Hangman(args.trim().equalsIgnoreCase("blind")));
//            }
        });
        //smore commands
        playCommands.add(new Command("help", "smelp"){
            @Override
            public String getHelpText(){
                return "Shows this help window";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                EmbedBuilder builder = createEmbed(jda.getSelfUser().getName()+" Help");
                if(Bot.prefixes.size()>1){
                    String prefx = "";
                    for(String s : Bot.prefixes){
                        prefx+="`"+s+"`\t";
                    }
                    builder.addField("Prefixes", prefx.trim(), false);
                }
                for(Command c : playCommands){
                    if(c.isSecret())continue;
                    builder.addField(prefixes.get(0)+c.command, c.getHelpText(), false);
                }
                channel.sendMessage(builder.build()).queue();
            }
        });
        playCommands.add(new Command("stop","abort","halt","cancel","finish"){
            @Override
            public String getHelpText(){
                return "Cancels your current action";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(SmoreBot.actions.containsKey(user.getIdLong())){
                    Action a = SmoreBot.actions.get(user.getIdLong());
                    a.cancel(channel);
                    if(a.cancelled)SmoreBot.actions.remove(user.getIdLong());
                }
            }
        });
        playCommands.add(new Command("stopgame","abortgame","haltgame","cancelgame","finishgame"){
            @Override
            public String getHelpText(){
                return "stops the currently running game";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                Game game = PlayBot.games.get(channel.getIdLong());
                if(game!=null){
                    if(!game.canAnyoneStop()&&user.getIdLong()!=210445638532333569l){
                        channel.sendMessage("You can't stop that game!").queue();
                        return;
                    }
                    game.running = false;
                    channel.sendMessage(game.getName()+" stopped.").queue();
                    game.stop(channel, StopReason.STOPGAME);
                    PlayBot.games.remove(channel.getIdLong());
                    return;
                }
                channel.sendMessage("There's no game running in this channel!").queue();
            }
        });
        playCommands.add(new Command("smore"){
            @Override
            public String getHelpText(){
                return "Mmmm, s'mores";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                SmoreBot.action(user, channel, new SmoreAction());
            }
        });
        playCommands.add(new SecretCommand("snore", "snooze", "sleep"){
            @Override
            public String getHelpText(){
                return "ZZzzzz";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(SmoreBot.actions.containsKey(user.getIdLong())){
                    Action a = SmoreBot.actions.get(user.getIdLong());
                    a.cancel(channel);
                    if(a.cancelled)SmoreBot.actions.remove(user.getIdLong());
                }
                SmoreBot.action(user, channel, new SnoozeAction());
            }
        });
        playCommands.add(new Command("give", "send", "pay"){
            @Override
            public String getHelpText(){
                return "Give <ping> <amount>\n"
                        + "Give someone your s'mores!";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                args = args.trim();
                if(args.isEmpty()){
                    channel.sendMessage("You give nobody nothing.").queue();
                    return;
                }
                long have = SmoreBot.getSmoreCount(user.getIdLong());
                if(have<=0){
                    channel.sendMessage("You don't have any s'mores!").queue();
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length==1){
                    try{
                        long amt = Long.parseLong(argses[0]);
                        if(have<amt)channel.sendMessage("You don't have "+amt+" s'more"+(amt==1?"":"s")+"!").queue();
                        else channel.sendMessage("You try to give nobody "+amt+" s'more"+(amt==1?"":"s")+", but nobody doesn't respond.").queue();
                    }catch(Exception ex){
                        channel.sendMessage("You try to give `"+argses[0].replace("`", "\\`")+"` nothing, but nothing happens.").queue();
                    }
                    return;
                }
                String target = argses[0];
                long targetID = 0;
                if(target.startsWith("<@")&&target.endsWith(">")){
                    if(target.contains("!"))target = target.substring(1);
                    try{
                        targetID = Long.parseLong(target.substring(2, target.length()-1));
                    }catch(Exception ex){
                        channel.sendMessage("Who?").queue();
                        return;
                    }
                }else{
                    try{
                        targetID = Long.parseLong(target);
                    }catch(Exception ex){
                        channel.sendMessage("Who?").queue();
                        return;
                    }
                }
                User targetUser;
                try{
                    targetUser = jda.retrieveUserById(targetID).complete();
                    if(targetUser==null){
                        channel.sendMessage("Who?").queue();
                        return;
                    }
                }catch(Exception ex){
                    channel.sendMessage("Who?").queue();
                    return;
                }
                long amt = 0;
                try{
                    amt = Long.parseLong(argses[1]);
                }catch(Exception ex){
                    channel.sendMessage("How many?").queue();
                    return;
                }
                if(have<amt){
                    channel.sendMessage("You don't have "+amt+" s'more"+(amt==1?"":"s")+"!").queue();
                    return;
                }
                if(amt<0){
                    channel.sendMessage("You can't give negative s'mores!").queue();
                    return;
                }
                if(amt==0){
                    channel.sendMessage("Nothing happens.").queue();
                    return;
                }
                channel.sendMessage("You gave "+nick(guild.retrieveMember(targetUser).complete())+" "+amt+" smore"+(amt==1?"":"s")+".").queue();
                SmoreBot.removeSmores(user, amt);
                SmoreBot.addSmores(targetUser, amt);
            }
        });
        playCommands.add(new Command("eat", "nom"){
            @Override
            public String getHelpText(){
                return "Yummy!";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                long have = SmoreBot.getSmoreCount(user.getIdLong());
                if(have<=0){
                    channel.sendMessage("You don't have any s'mores!").queue();
                    return;
                }
                long eat = 1;
                try{
                    eat = Long.parseLong(args.trim());
                }catch(NumberFormatException __){}
                if(have<eat){
                    channel.sendMessage("You don't have "+eat+" s'more"+(eat==1?"":"s")+"!").queue();
                    return;
                }
                if(eat<0){
                    channel.sendMessage("You can't eat negative s'mores!").queue();
                    return;
                }
                if(eat==0){
                    channel.sendMessage("You eat nothing. Nothing happens.").queue();
                    return;
                }
                if(eat>255){
                    channel.sendMessage("You can't eat more than 255 s'mores in a single *byte*!").queue();
                    return;
                }
                String[] noms = {" Nom nom nom.", " Tasty!", " Yum!"};
                channel.sendMessage("You eat "+eat+" s'more"+(eat==1?"":"s")+"."+noms[new Random().nextInt(noms.length)]).queue();
                SmoreBot.eatSmores(user, eat);
            }
        });
        playCommands.add(new SecretCommand("moresmore", "doublesmore"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                channel.sendMessage("If you tried making two s'mores at once, your arms would get tired and you'd drop them! You wouldn't want that, would you?").queue();
            }
        });
        playCommands.add(new SecretCommand("foursmore", "quadsmore"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                channel.sendMessage("To make four s'mores at once, you'd need four arms. You don't have four arms.").queue();
            }
        });
        playCommands.add(new SecretCommand("smorelord"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                for(Role role : guild.retrieveMember(user).complete().getRoles()){
                    if(role.getIdLong()==563124574032756746L){
                        if(SmoreBot.getSmoreCount(user.getIdLong())<0){
                            channel.sendMessage("You try to make a s'morelord, but you are stopped by the S'more bank. They want the S'mores you owe them.").queue();
                            return;
                        }
                        SmoreBot.action(user, channel, new SmoreLordAction());
                        return;
                    }
                }
                channel.sendMessage("You need a special S'mengineering degree to even comprehend smorelordship").queue();
            }
        });
        playCommands.add(new SecretCommand("glowshroom"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                Hut hut = SmoreBot.getHut(user.getIdLong());
                if(hut!=null&&hut.hasGlowshroom()){
                    channel.sendMessage("You bend down and carefully pick the glowshroom").queue();
                    hut.pickGlowshroom();
                    SmoreBot.addGlowshroom(user);
                    return;
                }
                channel.sendMessage("You don't see a glowshroom").queue();
            }
        });
        playCommands.add(new SecretCommand("actions"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                String actions = "";
                for(Long key : SmoreBot.actions.keySet()){
                    actions+=nick(guild.retrieveMemberById(key).complete())+" is "+SmoreBot.actions.get(key).getAction()+"\n";
                }
                if(actions.isEmpty())return;
                channel.sendMessage(actions).queue();
            }
        });
        playCommands.add(new Command("noms", "yums", "eated", "eaten", "eats"){
            @Override
            public String getHelpText(){
                return "Displays the amount of s'mores that you have consumed";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                channel.sendMessage(SmoreBot.getEatenCountS(user.getIdLong())).queue();
            }
        });
        playCommands.add(new Command("smores", "bal", "balance", "money"){
            @Override
            public String getHelpText(){
                return "Displays the amount of s'mores currently in your possession";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()==210445638532333569L){//it's thiz, check for more arguments
                    do{
                        args = args.trim();
                        if(args.isEmpty()){
                            break;
                        }
                        String[] argses = args.split(" ");
                        if(argses.length!=1){
                            break;
                        }
                        String target = argses[0];
                        long targetID = 0;
                        if(target.startsWith("<@")&&target.endsWith(">")){
                            if(target.contains("!"))target = target.substring(1);
                            try{
                                targetID = Long.parseLong(target.substring(2, target.length()-1));
                            }catch(Exception ex){
                                return;
                            }
                        }else{
                            try{
                                targetID = Long.parseLong(target);
                            }catch(Exception ex){
                                return;
                            }
                        }
                        User targetUser;
                        try{
                            targetUser = jda.retrieveUserById(targetID).complete();
                            if(targetUser==null){
                                return;
                            }
                        }catch(Exception ex){
                            return;
                        }
                        channel.sendMessage(SmoreBot.getSmoreCountS(targetUser.getIdLong())).queue();
                        return;
                    }while(false);
                }
                channel.sendMessage(SmoreBot.getSmoreCountS(user.getIdLong())).queue();
            }
        });
        playCommands.add(new SecretCommand("glowshrooms", "glows", "shrooms"){
            @Override
            public String getHelpText(){
                return "Displays the amount of glowshrooms currently in your possession";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(SmoreBot.getGlowshroomCount(user.getIdLong())>0)channel.sendMessage(SmoreBot.getGlowshroomCountS(user.getIdLong())).queue();
            }
        });
        playCommands.add(new Command("smoreboard", "leaderboard"){
            @Override
            public String getHelpText(){
                return "Displays the top 5 s'more s'tockpilers";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                ArrayList<Long> smorepilers = new ArrayList<>(SmoreBot.smores.keySet());
                Collections.sort(smorepilers, (Long o1, Long o2) -> (int)(SmoreBot.smores.get(o2)-SmoreBot.smores.get(o1)));
                for(Iterator<Long> it = smorepilers.iterator(); it.hasNext();){
                    try{
                        guild.retrieveMemberById(it.next()).complete();
                    }catch(ErrorResponseException ex){
                        it.remove();
                    }
                }
                EmbedBuilder builder = createEmbed("S'moreboard");
                String mess = "";
                for(int i = 0; i<Math.min(5, smorepilers.size()); i++){
                    mess+=nick(guild.retrieveMemberById(smorepilers.get(i)).complete())+": "+SmoreBot.getSmoreCountS(smorepilers.get(i))+"\n";
                }
                channel.sendMessage(builder.addField("Top S'more S'tockpilers", mess, false).build()).queue();
            }
        });
        playCommands.add(new SecretCommand("snoreboard"){
            @Override
            public String getHelpText(){
                return "Displays the bottom 5 s'more s'tockpilers";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                ArrayList<Long> smorepilers = new ArrayList<>(SmoreBot.smores.keySet());
                Collections.sort(smorepilers, (Long o1, Long o2) -> (int)(SmoreBot.smores.get(o1)-SmoreBot.smores.get(o2)));
                EmbedBuilder builder = createEmbed("S'noreboard");
                for(Iterator<Long> it = smorepilers.iterator(); it.hasNext();){
                    try{
                        guild.retrieveMemberById(it.next()).complete();
                    }catch(ErrorResponseException ex){
                        it.remove();
                    }
                }
                String mess = "";
                for(int i = 0; i<Math.min(5, smorepilers.size()); i++){
                    mess+=nick(guild.retrieveMemberById(smorepilers.get(i)).complete())+": "+SmoreBot.getSmoreCountS(smorepilers.get(i))+"\n";
                }
                channel.sendMessage(builder.addField("Top S'more Debtors", mess, false).build()).queue();
            }
        });
        playCommands.add(new Command("omnomboard", "nomboard", "yumboard", "eatboard", "snomnomnomboard", "nomnomnomboard", "omnomnomboard"){
            @Override
            public String getHelpText(){
                return "Displays the top 5 s'nomnomnommers";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                ArrayList<Long> snommers = new ArrayList<>(SmoreBot.eaten.keySet());
                Collections.sort(snommers, (Long o1, Long o2) -> (int)(SmoreBot.eaten.get(o2)-SmoreBot.eaten.get(o1)));
                for(Iterator<Long> it = snommers.iterator(); it.hasNext();){
                    try{
                        guild.retrieveMemberById(it.next()).complete();
                    }catch(ErrorResponseException ex){
                        it.remove();
                    }
                }
                EmbedBuilder builder = createEmbed("Nomboard");
                String mess = "";
                for(int i = 0; i<Math.min(5, snommers.size()); i++){
                    mess+=nick(guild.retrieveMemberById(snommers.get(i)).complete())+": "+SmoreBot.getEatenCountS(snommers.get(i))+"\n";
                }
                channel.sendMessage(builder.addField("Top S'nomnomnommers", mess, false).build()).queue();
            }
        });
        playCommands.add(new SecretCommand("glowboard", "glowshroomboard", "shroomboard"){
            @Override
            public String getHelpText(){
                return "Displays the top 5 glowshroom pickers";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                ArrayList<Long> glowshroompilers = new ArrayList<>(SmoreBot.glowshrooms.keySet());
                Collections.sort(glowshroompilers, (Long o1, Long o2) -> (int)(SmoreBot.glowshrooms.get(o2)-SmoreBot.glowshrooms.get(o1)));
                for(Iterator<Long> it = glowshroompilers.iterator(); it.hasNext();){
                    try{
                        guild.retrieveMemberById(it.next()).complete();
                    }catch(ErrorResponseException ex){
                        it.remove();
                    }
                }
                EmbedBuilder builder = createEmbed("Glowboard");
                String mess = "";
                for(int i = 0; i<Math.min(5, glowshroompilers.size()); i++){
                    mess+=nick(guild.retrieveMemberById(glowshroompilers.get(i)).complete())+": "+SmoreBot.getGlowshroomCountS(glowshroompilers.get(i))+"\n";
                }
                channel.sendMessage(builder.addField("Top Glowshroom Pickers", mess, false).build()).queue();
            }
        });
        //hut commands
        playCommands.add(new SecretCommand("hutdump"){
            @Override
            public String getHelpText(){
                return "Get a debug dump of all your hut stuff";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(SmoreBot.hutBunches.containsKey(user.getIdLong())){
                    Hut hut = SmoreBot.getHut(user.getIdLong());
                    String mess = "";
                    for(HutThing thing : hut.getFurniture()){
                        float scale = hut.getScale(thing.y+thing.getDimY()/2f);
                        mess+="\n ";
                        if(thing instanceof HutThingColorable){
                            Color c = ((HutThingColorable)thing).getColor();
                            mess+="#"+Integer.toHexString(c.getRed())+Integer.toHexString(c.getGreen())+Integer.toHexString(c.getBlue())+" ";
                        }
                        mess+=thing.getName()+" @ ("+thing.x+","+thing.y+","+thing.z+" x"+scale+" | "+thing.uuid+")";
                        if(thing.parent!=null)mess+="; parent: "+thing.parent;
                    }
                    channel.sendMessage(mess.trim()).queue();
               }else{
                    channel.sendMessage("You don't have a hut!").queue();
                }
            }
        });
        playCommands.add(new Command("hut", "home", "house"){
            @Override
            public String getHelpText(){
                return "View your or someone else's hut";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                args = args.trim();
                User targetUser = null;
                if(args.startsWith("<@")&&args.endsWith(">")){
                    if(args.contains("!"))args = args.substring(1);
                    try{
                        targetUser = jda.retrieveUserById(Long.parseLong(args.substring(2, args.length()-1))).complete();
                    }catch(Exception ex){}
                }else{
                    try{
                        targetUser = jda.retrieveUserById(Long.parseLong(args)).complete();
                    }catch(Exception ex){}
                }
                if(targetUser==null){
                    long id = user.getIdLong();
                    if(SmoreBot.hutBunches.containsKey(id)){
                        Hut hut = SmoreBot.getHut(id);
                        hut.sendExteriorImage(channel);
                        hut.sendInteriorImage(channel);
                    }else{
                        channel.sendMessage("You don't have a hut!").queue();
                    }
                }else{
                    long id = targetUser.getIdLong();
                    if(SmoreBot.hutBunches.containsKey(id)){
                        Hut hut = SmoreBot.getHut(id);
                        hut.sendExteriorImage(channel);
                        hut.sendInteriorImage(channel);
                    }else{
                        channel.sendMessage("You look for a hut belonging to "+nick(guild.retrieveMember(targetUser).complete())+", but you don't find one").queue();
                    }
                }
            }
        });
        playCommands.add(new Command("move"){
            @Override
            public String getHelpText(){
                return "Move something in your hut";
            }
            @Override
            public void run(User user, MessageChannel channel, String commandArg, boolean debug){
                String[] args = commandArg.trim().split(" ");
                if(SmoreBot.hutBunches.containsKey(user.getIdLong())){
                    Hut hut = SmoreBot.getHut(user.getIdLong());
                    if(commandArg.isEmpty()){
                        channel.sendMessage("Move what?").queue();
                        return;
                    }
                    ArrayList<HutThing> possible = new ArrayList<>();
                    String chosen = args[0];
                    LOOP:for(HutThing thing : hut.getFurniture()){
                        for(int i = 1; i<args.length; i++){
                            String str = "";
                            for(int j = 0; j<=i; j++){
                                str+=args[j]+" ";
                            }
                            if(thing.getName().equalsIgnoreCase(str.trim())){
                                chosen = thing.getName();
                                String[] newargs = new String[args.length-i];
                                for(int j = 0; j<newargs.length; j++){
                                    newargs[j] = args[j+i];
                                }
                                args = newargs;
                                break LOOP;
                            }
                        }
                    }
                    for(HutThing thing : hut.getFurniture()){
                        if(thing.getName().equalsIgnoreCase(chosen))possible.add(thing);
                    }
                    if(possible.isEmpty()){
                        channel.sendMessage("You don't have that!").queue();
                        return;
                    }
                    HutThing thingToMove;
                    String whereTo = null;
                    if(possible.size()>1){
                        boolean whichOne = false;
                        if(args.length==1){
                            whichOne = true;
                        }
                        int idx = 0;
                        if(!whichOne){
                            try{
                                idx = Integer.parseInt(args[1]);
                            }catch(NumberFormatException ex){
                                whichOne = true;
                            }
                        }
                        if(idx<1||idx>possible.size())whichOne = true;
                        if(whichOne){
                            channel.sendMessage("Which one?").queue();
                            hut.sendHighlightImage(channel, possible);
                            return;
                        }
                        thingToMove = possible.get(idx-1);
                        if(args.length>2)whereTo = args[2];
                    }else{
                        thingToMove = possible.get(0);
                        if(args.length>1)whereTo = args[1];
                    }
                    for(HutThing thing : hut.getFurniture()){
                        if(thing.parent!=null&&thing.parent.equals(thingToMove.uuid)){
                            channel.sendMessage("You can't move that!").queue();
                            return;
                        }
                    }
                    boolean butWhere = false;
                    if(whereTo==null){
                        butWhere = true;
                    }
                    ArrayList<Placement> possiblePlacements = hut.getPossiblePlacements(thingToMove);
                    int idx = 0;
                    if(!butWhere){
                        try{
                            idx = Integer.parseInt(whereTo);
                        }catch(NumberFormatException ex){
                            butWhere = true;
                        }
                    }
                    if(idx<1||idx>possiblePlacements.size())butWhere = true;
                    if(butWhere){
                        channel.sendMessage("Move it where?").queue();
                        hut.sendPlacementHighlightImage(channel, thingToMove, possiblePlacements);
                        return;
                    }
                    Placement there = possiblePlacements.get(idx-1);
                    thingToMove.x = there.x;
                    thingToMove.y = there.y;
                    thingToMove.z = there.z;
                    thingToMove.wall = there.wall;
                    thingToMove.parent = there.parent;
                    channel.sendMessage("You move the "+thingToMove.getName()+".").queue();
                }else{
                    channel.sendMessage("You don't have a hut!").queue();
                }
            }
        });
        playCommands.add(new Command("store", "shop"){
            @Override
            public String getHelpText(){
                return "Browse the s'tore";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                EmbedBuilder builder = createEmbed("S'tore");
                String names = "";
                String prices = "";
                String colors = "";
                HutBunch bunch = SmoreBot.hutBunches.get(user.getIdLong());
                if(bunch==null){
                    for(HutType type : HutType.values()){
                        names+=type.name+"\n";
                        prices+=type.getPrice()+"\n";
                        colors+="\n";
                    }
                }else{
                    FOR:for(HutType type : HutType.values()){
                        for(Hut hut : bunch.huts){
                            if(hut.type==type)continue FOR;
                        }
                        names+=type.name+"\n";
                        prices+=type.getPrice()+"\n";
                        colors+="\n";
                    }
                    FOR:for(HutThing thing : Hut.allFurniture){
                        if(!thing.isSellable())continue;
                        names+=thing.getName()+"\n";
                        prices+=thing.getPrice()+"\n";
                        colors+=(thing instanceof HutThingColorable?"Colorable":"​​​​​​​")+"\n";
                    }
                }
                builder.addField("Item", names, true);
                builder.addField(smoremoji(), prices, true);
                builder.addField("", colors, true);
                channel.sendMessage(builder.build()).queue();
            }
        });
        playCommands.add(new Command("buy", "purchace", "get"){
            @Override
            public String getHelpText(){
                return "Buy something from the store\nTo buy a colored item, use -buy <color> <item name>\nColors must be a hex color (such as #038c3f)";
            }
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                long price = -1;
                Runnable onBuy = null;
                for(HutType type : HutType.values()){
                    if(args.trim().equalsIgnoreCase(type.name)){
                        if(SmoreBot.hasHut(user.getIdLong(), type)){
                            channel.sendMessage("You already have a "+type.name+"!").queue();
                            return;
                        }
                        price = type.getPrice();
                        onBuy = () -> {
                            if(SmoreBot.hutBunches.containsKey(user.getIdLong())){
                                HutBunch bunch = SmoreBot.hutBunches.get(user.getIdLong());
                                bunch.huts.add(new Hut(bunch, type));
                                channel.sendMessage("You buy a "+type.name+" nearby the "+type.campfireName+". It's currently empty, but you can get stuff to put in it.").queue();
                            }else{
                                HutBunch bunch = new HutBunch(user.getIdLong());
                                bunch.huts.add(new Hut(bunch, type));
                                SmoreBot.hutBunches.put(user.getIdLong(), bunch);
                            }
                        };
                    }
                }
                if(SmoreBot.hutBunches.containsKey(user.getIdLong())){
                    Hut hut = SmoreBot.getHut(user.getIdLong());
                    FOR:for(HutThing thing : Hut.allFurniture){
                        if(!thing.isSellable())continue;
                        if(args.trim().replace("_", " ").equalsIgnoreCase(thing.getName())){
                            price = thing.getPrice();
                            final long pric = price;
                            onBuy = () -> {
                                if(hut.add(thing.newInstance(hut))){
                                    channel.sendMessage("You buy a "+thing.getName()+" and put it in your hut").queue();
                                }else{
                                    channel.sendMessage("There's no space for a "+thing.getName()+" in your hut!").queue();
                                    SmoreBot.addSmores(user, pric);
                                }
                            };
                        }
                        if(thing instanceof HutThingColorable&&args.contains(" ")){
                            String[] strs = args.split(" ", 2);
                            if(strs[1].trim().equalsIgnoreCase(thing.getName())){
                                Color color = null;
                                String hex = strs[0];
                                if(hex.startsWith("#"))hex = hex.substring(1);
                                if(hex.length()==6){
                                    String r = hex.substring(0, 2);
                                    String g = hex.substring(2, 4);
                                    String b = hex.substring(4, 6);
                                    try{
                                        color = new Color(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
                                    }catch(Exception ex){}
                                }
                                if(color==null){
                                    channel.sendMessage("What color?").queue();
                                    return;
                                }
                                Color c = color;
                                price = thing.getPrice();
                                onBuy = () -> {
                                    channel.sendMessage("You buy a "+thing.getName()+" and put it in your hut").queue();
                                    hut.add(((HutThingColorable)thing.newInstance(hut)).setColor(c));
                                };
                            }
                        }
                    }
                }
                if(onBuy==null){
                    channel.sendMessage("That's not for sale!").queue();
                    return;
                }
                if(SmoreBot.getSmoreCount(user.getIdLong())<price){
                    channel.sendMessage("You don't have enough s'mores!").queue();
                    return;
                }
                SmoreBot.removeSmores(user, price);
                onBuy.run();
            }
        });
        playCommands.add(new Command("sell"){
            @Override
            public String getHelpText(){
                return "Sell something you don't need anymore and get some of the price back";
            }
            @Override
            public void run(User user, MessageChannel channel, String commandArg, boolean debug){
                String[] args = commandArg.trim().split(" ");
                if(SmoreBot.hutBunches.containsKey(user.getIdLong())){
                    for(Hut hut : SmoreBot.hutBunches.get(user.getIdLong()).huts){
                        if(commandArg.trim().equalsIgnoreCase(hut.type.name)){
                            channel.sendMessage("You can't sell your hut!").queue();
                            return;
                        }
                    }
                    Hut hut = SmoreBot.getHut(user.getIdLong());
                    if(commandArg.isEmpty()){
                        channel.sendMessage("Sell what?").queue();
                        return;
                    }
                    ArrayList<HutThing> possible = new ArrayList<>();
                    String chosen = args[0];
                    LOOP:for(HutThing thing : hut.getFurniture()){
                        for(int i = 1; i<args.length; i++){
                            String str = "";
                            for(int j = 0; j<=i; j++){
                                str+=args[j]+" ";
                            }
                            if(thing.getName().equalsIgnoreCase(str.trim())){
                                chosen = thing.getName();
                                String[] newargs = new String[args.length-i];
                                for(int j = 0; j<newargs.length; j++){
                                    newargs[j] = args[j+i];
                                }
                                args = newargs;
                                break LOOP;
                            }
                        }
                    }
                    for(HutThing thing : hut.getFurniture()){
                        if(thing.getName().equalsIgnoreCase(chosen))possible.add(thing);
                    }
                    if(possible.isEmpty()){
                        channel.sendMessage("You don't have that!").queue();
                        return;
                    }
                    HutThing thing;
                    if(possible.size()>1){
                        boolean whichOne = false;
                        if(args.length==1){
                            whichOne = true;
                        }
                        int idx = 0;
                        if(!whichOne){
                            try{
                                idx = Integer.parseInt(args[1]);
                            }catch(NumberFormatException ex){
                                whichOne = true;
                            }
                        }
                        if(idx<1||idx>possible.size())whichOne = true;
                        if(whichOne){
                            channel.sendMessage("Which one?").queue();
                            hut.sendHighlightImage(channel, possible);
                            return;
                        }
                        thing = possible.get(idx-1);
                    }else{
                        thing = possible.get(0);
                    }
                    if(!thing.isSellable()){
                        channel.sendMessage("That cannot be sold!").queue();
                        return;
                    }
                    for(HutThing thing2 : hut.getFurniture()){
                        if(thing2.parent!=null&&thing2.parent.equals(thing.uuid)){
                            channel.sendMessage("You can't sell that!").queue();
                            return;
                        }
                    }
                    SmoreBot.addSmores(user, thing.getPrice()/2);
                    channel.sendMessage("You sell your "+thing.getName()).queue();
                    hut.remove(thing);
                }else{
                    channel.sendMessage("You have nothing to sell!").queue();
                }
            }
        });
        playCommands.add(new Command("switch", "switchhut", "moveto", "movein", "changehut"){
            @Override
            public String getHelpText(){
                return "Move to a different hut";
            }
            @Override
            public void run(User user, MessageChannel channel, String commandArg, boolean debug){
                HutBunch bunch = SmoreBot.hutBunches.get(user.getIdLong());
                if(bunch==null){
                    channel.sendMessage("You don't have a hut!").queue();
                    return;
                }
                if(bunch.huts.size()==1){
                    channel.sendMessage("You don't have another hut!").queue();
                    return;
                }
                for(HutType type : HutType.values()){
                    if(commandArg.trim().equalsIgnoreCase(type.name)||commandArg.trim().equalsIgnoreCase(type.name())){
                        Hut hut = null;
                        for(Hut h : bunch.huts){
                            if(h.type==type)hut = h;
                        }
                        if(hut==null){
                            channel.sendMessage("You don't have a "+type.name+"!").queue();
                        }else{
                            int idx = bunch.huts.indexOf(hut);
                            if(bunch.mainHut==idx){
                                channel.sendMessage("You're already in your "+type.name).queue();
                            }else{
                                bunch.mainHut = idx;
                                channel.sendMessage("You move to your "+type.name).queue();
                            }
                        }
                        return;
                    }
                }
                String huts = "";
                for(Hut hut : bunch.huts){
                    huts+=", "+hut.type.name;
                }
                channel.sendMessage("Switch to which?\n"+huts.substring(2)).queue();
            }
        });
        playCommands.add(new Command("lights", "light", "lightswitch"){
            @Override
            public String getHelpText(){
                return "Turn on/off lights";
            }
            @Override
            public void run(User user, MessageChannel channel, String commandArg, boolean debug){
                Hut hut = SmoreBot.getHut(user.getIdLong());
                if(hut==null){
                    channel.sendMessage("You don't have a hut!").queue();
                    return;
                }
                Boolean lights = null;
                for(HutThing thing : hut.furniture){
                    if(thing.isLightSwitch()){
                        lights = thing.isOn();
                    }
                }
                if(lights==null){
                    channel.sendMessage("You don't have a light switch!").queue();
                    return;
                }
                boolean on = !lights;
                channel.sendMessage("You turn "+(on?"on":"off")+" the lights").queue();
                for(HutThing thing : hut.furniture){
                    if(thing.isLightSwitch()||thing.isLamp()){
                        thing.setOn(on);
                    }
                }
            }
        });
        //admin commands
        playCommands.add(new SecretCommand("transferhut"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()!=210445638532333569L)return;//not thiz
                args = args.trim();
                if(args.isEmpty()){
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length==1){
                    return;
                }
                String target1 = argses[0];
                long target1ID = 0;
                if(target1.startsWith("<@")&&target1.endsWith(">")){
                    if(target1.contains("!"))target1 = target1.substring(1);
                    try{
                        target1ID = Long.parseLong(target1.substring(2, target1.length()-1));
                    }catch(Exception ex){
                        return;
                    }
                }else{
                    try{
                        target1ID = Long.parseLong(target1);
                    }catch(Exception ex){
                        return;
                    }
                }
                User target1User;
                try{
                    target1User = jda.retrieveUserById(target1ID).complete();
                    if(target1User==null){
                        return;
                    }
                }catch(Exception ex){
                    return;
                }
                String target2 = argses[1];
                long target2ID = 0;
                if(target2.startsWith("<@")&&target2.endsWith(">")){
                    if(target2.contains("!"))target2 = target2.substring(1);
                    try{
                        target2ID = Long.parseLong(target2.substring(2, target2.length()-1));
                    }catch(Exception ex){
                        return;
                    }
                }else{
                    try{
                        target2ID = Long.parseLong(target2);
                    }catch(Exception ex){
                        return;
                    }
                }
                User target2User;
                try{
                    target2User = jda.retrieveUserById(target2ID).complete();
                    if(target2User==null){
                        return;
                    }
                }catch(Exception ex){
                    return;
                }
                target1ID = target1User.getIdLong();
                target2ID = target2User.getIdLong();
                if(!SmoreBot.hutBunches.containsKey(target1ID)){
                    channel.sendMessage(nick(guild.retrieveMember(target1User).complete())+" doesn't have a hut!").queue();
                    return;
                }
                if(SmoreBot.hutBunches.containsKey(target2ID)){
                    channel.sendMessage(nick(guild.retrieveMember(target2User).complete())+" already has a hut!").queue();
                    return;
                }
                if(SmoreBot.hutBunches.containsKey(target1ID)){
                    HutBunch bunch = SmoreBot.hutBunches.get(target1ID);
                    SmoreBot.hutBunches.remove(target1ID);
                    bunch.owner = target2ID;
                    SmoreBot.hutBunches.put(target2ID, bunch);
                }
            }
        });
        playCommands.add(new SecretCommand("delete"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()!=210445638532333569L)return;//not thiz
                args = args.trim();
                if(args.isEmpty()){
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length!=1){
                    return;
                }
                String target = argses[0];
                long targetID = 0;
                if(target.startsWith("<@")&&target.endsWith(">")){
                    if(target.contains("!"))target = target.substring(1);
                    try{
                        targetID = Long.parseLong(target.substring(2, target.length()-1));
                    }catch(Exception ex){
                        return;
                    }
                }else{
                    try{
                        targetID = Long.parseLong(target);
                    }catch(Exception ex){
                        return;
                    }
                }
                User targetUser;
                try{
                    targetUser = jda.retrieveUserById(targetID).complete();
                    if(targetUser==null){
                        return;
                    }
                }catch(Exception ex){
                    return;
                }
                targetID = targetUser.getIdLong();
                if(SmoreBot.getSmoreCount(targetID)!=0)throw new NonZeroSmoreException(nick(guild.retrieveMember(targetUser).complete())+" has non-zero s'mores!");
                if(SmoreBot.getEatenCount(targetID)!=0)throw new NonZeroEatenException(nick(guild.retrieveMember(targetUser).complete())+" has non-zero eaten s'mores!");
                SmoreBot.smores.remove(targetID);
                SmoreBot.eaten.remove(targetID);
            }
        });
        playCommands.add(new SecretCommand("seteat"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()!=210445638532333569L)return;//not thiz
                args = args.trim();
                if(args.isEmpty()){
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length==1){
                    return;
                }
                String target = argses[0];
                long targetID = 0;
                if(target.startsWith("<@")&&target.endsWith(">")){
                    if(target.contains("!"))target = target.substring(1);
                    try{
                        targetID = Long.parseLong(target.substring(2, target.length()-1));
                    }catch(Exception ex){
                        return;
                    }
                }else{
                    try{
                        targetID = Long.parseLong(target);
                    }catch(Exception ex){
                        return;
                    }
                }
                User targetUser;
                try{
                    targetUser = jda.retrieveUserById(targetID).complete();
                    if(targetUser==null){
                        return;
                    }
                }catch(Exception ex){
                    return;
                }
                long amt = 0;
                try{
                    amt = Long.parseLong(argses[1]);
                }catch(Exception ex){
                    return;
                }
                if(amt<0){
                    return;
                }
                channel.sendMessage("set "+nick(guild.retrieveMember(targetUser).complete())+" to "+amt+" eated s'more"+(amt==1?"":"s")+".").queue();
                SmoreBot.eaten.put(targetUser.getIdLong(), amt);
            }
        });
        playCommands.add(new SecretCommand("setsmores"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()!=210445638532333569L)return;//not thiz
                args = args.trim();
                if(args.isEmpty()){
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length==1){
                    return;
                }
                String target = argses[0];
                long targetID = 0;
                if(target.startsWith("<@")&&target.endsWith(">")){
                    if(target.contains("!"))target = target.substring(1);
                    try{
                        targetID = Long.parseLong(target.substring(2, target.length()-1));
                    }catch(Exception ex){
                        return;
                    }
                }else{
                    try{
                        targetID = Long.parseLong(target);
                    }catch(Exception ex){
                        return;
                    }
                }
                User targetUser;
                try{
                    targetUser = jda.retrieveUserById(targetID).complete();
                    if(targetUser==null){
                        return;
                    }
                }catch(Exception ex){
                    return;
                }
                long amt = 0;
                try{
                    amt = Long.parseLong(argses[1]);
                }catch(Exception ex){
                    return;
                }
                channel.sendMessage("set "+nick(guild.retrieveMember(targetUser).complete())+" to "+amt+" s'more"+(amt==1?"":"s")+".").queue();
                SmoreBot.smores.put(targetUser.getIdLong(), amt);
            }
        });
        playCommands.add(new SecretCommand("setglowshrooms"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()!=210445638532333569L)return;//not thiz
                args = args.trim();
                if(args.isEmpty()){
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length==1){
                    return;
                }
                String target = argses[0];
                long targetID = 0;
                if(target.startsWith("<@")&&target.endsWith(">")){
                    if(target.contains("!"))target = target.substring(1);
                    try{
                        targetID = Long.parseLong(target.substring(2, target.length()-1));
                    }catch(Exception ex){
                        return;
                    }
                }else{
                    try{
                        targetID = Long.parseLong(target);
                    }catch(Exception ex){
                        return;
                    }
                }
                User targetUser;
                try{
                    targetUser = jda.retrieveUserById(targetID).complete();
                    if(targetUser==null){
                        return;
                    }
                }catch(Exception ex){
                    return;
                }
                long amt = 0;
                try{
                    amt = Long.parseLong(argses[1]);
                }catch(Exception ex){
                    return;
                }
                channel.sendMessage("set "+nick(guild.retrieveMember(targetUser).complete())+" to "+amt+" glowshroom"+(amt==1?"":"s")+".").queue();
                SmoreBot.glowshrooms.put(targetUser.getIdLong(), amt);
            }
        });
        playCommands.add(new SecretCommand("setmaxgames", "setgamelimit"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                if(user.getIdLong()!=210445638532333569L)return;//not thiz
                args = args.trim();
                if(args.isEmpty()){
                    return;
                }
                String[] argses = args.split(" ");
                if(argses.length!=1){
                    return;
                }
                int max = 0;
                try{
                    max = Integer.parseInt(argses[0]);
                }catch(Exception ex){
                    return;
                }
                channel.sendMessage("set concurrent game limit to "+max).queue();
                PlayBot.maxGames = max;
            }
        });
        SecretCommand cookie = new SecretCommand("cookie"){
            @Override
            public void run(User user, MessageChannel channel, String args, boolean debug){
                cookies++;
            }
        };
        botCommands.add(cookie);
        playCommands.add(cookie);
        */
    }
    private static String nick(Member member){
        if(member==null)return ":shrug:";
        String name = member.getNickname();
        if(name==null)name = member.getUser().getName();
        return "`"+name.replace("`", "")+"`";
    }
    @Override
    public void onGatewayPing(GatewayPingEvent event){
        super.onGatewayPing(event);
        SmoreBot.save();
    }
    @Override
    public void onReady(ReadyEvent event){
        Thread channelRead = new Thread(() -> {
            int[] bytes = new int[1];
            int totalCount = 0;
            for(Long id : dataChannels){
                TextChannel channel = jda.getTextChannelById(id);
                if(channel==null){
                    System.err.println("Invalid channel: "+id);
                    continue;
                }
                System.out.println("Reading channel: "+channel.getName());
                MessageHistory history = channel.getHistory();
                while(!history.retrievePast(100).complete().isEmpty());
                System.out.println("Scanning...");
                final int[] count = new int[1];
                ArrayList<Message> messages = new ArrayList<>(history.getRetrievedHistory());
                Stack<Message> stak = new Stack<>();
                for(Message m : messages){
                    stak.push(m);
                }
                messages.clear();
                while(!stak.isEmpty())messages.add(stak.pop());
                int total = 0;
                int[] done = new int[1];
                for(Message message : messages){
                    total++;
                    Thread t = new Thread(() -> {
                        try{
                            storeMultiblocks(message);
                            for(Attachment att : message.getAttachments()){
                                if(att==null||att.getFileExtension()==null)continue;
                                if(att.getFileExtension().equalsIgnoreCase("json")){
                                    count[0]++;
                                    System.out.println("Found "+att.getFileName()+" ("+count[0]+")");
                                    bytes[0]+=att.getSize();
                                }
                            }
                        }catch(Throwable throwable){
                            done[0]++;
                        }
                        done[0]++;
                    }, "Multiblock parsing thread "+UUID.randomUUID().toString());
                    t.setDaemon(true);
                    t.start();
                }
                while(done[0]<total){
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException ex){}
                }
                System.out.println("Finished Reading channel: "+channel.getName()+". Multiblocks: "+count[0]);
                totalCount+=count[0];
            }
            System.out.println("Total Multiblocks: "+totalCount);
            System.out.println("Total Size: "+bytes[0]);
        }, "Channel reading thread");
        channelRead.setDaemon(true);
        channelRead.start();
    }
    public void storeMultiblocks(Message message){
        for(Attachment att : message.getAttachments()){
            if(att!=null&&att.getFileExtension()!=null){
                switch(att.getFileExtension().toLowerCase(Locale.ROOT)){
                    case "png":
                    case "gif":
                    case "jpg":
                    case "xlsx":
                    case "txt":
                        continue;
                }
            }
            try{
                Project ncpf = FileReader.read(() -> {
                    try{
                        return att.retrieveInputStream().get();
                    }catch(InterruptedException|ExecutionException ex){
                        throw new RuntimeException(ex);
                    }
                });
                if(ncpf!=null){
                    if(ncpf.configuration!=null){
                        if(!ncpf.configuration.nameMatches(Configuration.configurations.get(0))){
                            System.err.println("Configuration "+ncpf.configuration.name+" does not match! Skipping "+att.getFileName());
                            continue;
                        }
                        if(!ncpf.configuration.addons.isEmpty()){
                            System.err.println("Configuration "+ncpf.configuration.getFullName()+" has addons! Skipping "+att.getFileName());
                            continue;
                        }
                    }
                    ncpf.configuration = Configuration.configurations.get(0);
                    for(Multiblock m : ncpf.multiblocks){
                        m.convertTo(ncpf.configuration);
                    }
                    synchronized(storedMultiblocks){
                        ncpf.metadata.put("Original Source", message.getJumpUrl());
                        for(Multiblock m : ncpf.multiblocks){
                            m.metadata.put("Original Source", message.getJumpUrl());
                        }
                        storedMultiblocks.add(ncpf);
                    }
                }
            }catch(Exception ex){
                System.err.println("Failed to read file: "+(att==null?null:att.getFileName()));
            }
        }
    }
    private static EmbedBuilder createEmbed(String title){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setColor(new Color(255, 200, 0).getRGB());
        builder.setFooter("Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator/releases");
        return builder;
    }
    public static void start(String[] args){
        System.out.println("Loading S'more bank...");
        SmoreBot.load();
        System.out.println("Loading channels...");
        for(int i = 0; i<args.length; i++){
            String arg = args[i];
            if(arg.equalsIgnoreCase(Main.discordBotToken))continue;
            switch(arg){
                case "headless":
                case "noAWT":
                case "noAWTDuringStartup":
                case "discord":
                case "vr":
                    continue;
            }
            if(arg.startsWith("bot")){
                long c = Long.parseLong(arg.substring(3));
                botChannels.add(c);
                System.out.println("Added bot channel: "+c);
            }
            else if(arg.startsWith("play")){
                long c = Long.parseLong(arg.substring(4));
                playChannels.add(c);
                System.out.println("Added play channel: "+c);
            }
            else if(arg.startsWith("data")){
                long c = Long.parseLong(arg.substring(4));
                dataChannels.add(c);
                System.out.println("Added data channel: "+c);
            }
            else{
                prefixes.add(arg);
                System.out.println("Added prefix: "+arg);
            }
        }
        if(prefixes.isEmpty())prefixes.add("-");
        System.out.println("Loading bot specialties...");
        config = Config.newConfig(new File(new File("special.dat").getAbsolutePath()));
        config.load();
        cookies = config.get("cookies", 0);
        System.out.println("Starting bot...");
        JDABuilder b = JDABuilder.createDefault(Main.discordBotToken);
        b.setActivity(Activity.watching("cookies accumulate ("+cookies+")"));
        b.addEventListeners(new Bot());
        try{
            jda = b.build();
            System.out.println("Bot started!");
            FileWriter.botRunning = true;
        }catch(LoginException ex){
            Core.criticalError("Failed to log in!", ex);
        }
    }
    public static void stop(){
        SmoreBot.save();
        config = Config.newConfig(new File(new File("special.dat").getAbsolutePath()));
        config.set("cookies", cookies);
        config.save();
        if(jda!=null)jda.shutdownNow();
        jda = null;
        FileWriter.botRunning = false;
    }
    public static void render2D(){
        if(pendingImage!=null){
            try{
                image = Core.makeImage(imgWidth, imgHeight, pendingImage);
            }catch(Exception ex){
                error = new RuntimeException(ex);
            }
            pendingImage = null;
        }
    }
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        guild = event.getGuild();
        if(dataChannels.contains(event.getChannel().getIdLong()))storeMultiblocks(event.getMessage());//store own posts too :3
        if(event.getAuthor().isBot())return;
        if(botChannels.contains(event.getChannel().getIdLong())){
            String command = event.getMessage().getContentRaw();
            boolean hasPrefix = false;
            for(String prefix : prefixes){
                if(command.startsWith(prefix)){
                    command = command.substring(prefix.length());
                    hasPrefix = true;
                    break;
                }
            }
            if(hasPrefix){
                command = command.replace("’", "'").replace("s'", "s");
                try{
                    for(Command cmd : botCommands){
                        for(String alt : cmd.alternates){
                            if(command.equalsIgnoreCase(alt)||command.startsWith(alt+" ")){
                                String args = command.substring(alt.length()).trim();
                                try{
                                    cmd.run(event.getAuthor(), event.getChannel(), args, debug);
                                }catch(Exception ex){
                                    printErrorMessage(event.getChannel(), "Caught exception running command `"+alt+"`!", ex);
                                }
                            }
                        }
                    }
                }catch(Exception ex){
                    printErrorMessage(event.getChannel(), "Caught exception loading command!", ex);
                }
            }
        }
        if(playChannels.contains(event.getChannel().getIdLong())){
            Game game = PlayBot.games.get(event.getChannel().getIdLong());
            if(game!=null){
                if(event.getMessage().getContentDisplay().equals("(╯°□°）╯︵ ┻━┻")){
                    event.getChannel().sendMessage("┬─┬ ノ( ゜-゜ノ)").queue();
                }
                game.onMessage(event.getMessage());
            }
            String command = event.getMessage().getContentRaw();
            boolean hasPrefix = false;
            for(String prefix : prefixes){
                if(command.startsWith(prefix)){
                    command = command.substring(prefix.length());
                    hasPrefix = true;
                    break;
                }
            }
            if(hasPrefix){
                command = command.replace("’", "'").replace("s'", "s");
                try{
                    for(Command cmd : playCommands){
                        for(String alt : cmd.alternates){
                            if(command.equalsIgnoreCase(alt)||command.startsWith(alt+" ")){
                                String args = command.substring(alt.length()).trim();
                                try{
                                    cmd.run(event.getAuthor(), event.getChannel(), args, debug);
                                }catch(Exception ex){
                                    printErrorMessage(event.getChannel(), "Caught exception running command `"+alt+"`!", ex);
                                }
                            }
                        }
                    }
                }catch(Exception ex){
                    printErrorMessage(event.getChannel(), "Caught exception loading command!", ex);
                }
            }
        }
    }
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
        if(event.getAuthor().getIdLong()!=210445638532333569l)return;//THIZ ONLY
        if(playChannels.contains(event.getChannel().getIdLong())){
            String command = event.getMessage().getContentRaw();
            boolean hasPrefix = false;
            for(String prefix : prefixes){
                if(command.startsWith(prefix)){
                    command = command.substring(prefix.length());
                    hasPrefix = true;
                    break;
                }
            }
            if(!hasPrefix)return;
            try{
                for(Command cmd : playCommands){
                    for(String alt : cmd.alternates){
                        if(command.equalsIgnoreCase(alt)||command.startsWith(alt+" ")){
                            String args = command.substring(alt.length()).trim();
                            try{
                                cmd.run(event.getAuthor(), event.getChannel(), args, debug);
                            }catch(Exception ex){
                                printErrorMessage(event.getChannel(), "Caught exception running command `"+alt+"`!", ex);
                            }
                        }
                    }
                }
            }catch(Exception ex){
                printErrorMessage(event.getChannel(), "Caught exception loading command!", ex);
            }
        }
    }
    public static void printErrorMessage(MessageChannel channel, String message, Throwable ex){
        String trace = "";
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for(StackTraceElement e : stackTrace){
            String[] splitClassName = e.getClassName().split("\\Q.");
            String filename = splitClassName[splitClassName.length-1]+".java";
            String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
            if((trace+nextLine).length()+4>1024){
                trace+="\n...";
                break;
            }else trace+=nextLine;
        }
        channel.sendMessage(createEmbed(message).addField(ex.getClass().getName()+": "+ex.getMessage(), trace.trim(), false).build()).queue();
        Throwable t = ex.getCause();
        if(t!=null)printErrorMessage(channel, "Caused by: "+t.getClass().getName(), t);
    }
    private static int imgWidth, imgHeight;
    private static BufferRenderer pendingImage = null;
    private static Image image = null;
    private static RuntimeException error = null;
    public static Image makeImage(int width, int height, BufferRenderer r){
        imgWidth = width;
        imgHeight = height;
        pendingImage = r;
        image = null;
        error = null;
        while(image==null&&error==null){
            try{
                Thread.sleep(10);
            }catch(InterruptedException ex){}
        }
        if(error!=null)throw error;
        return image;
    }
    private static String smoremoji(){
        return "<:smore:493612965195677706>";
    }
    private static class NonZeroSmoreException extends RuntimeException{
        public NonZeroSmoreException(String message){
            super(message);
        }
    }
    private static class NonZeroEatenException extends RuntimeException{
        public NonZeroEatenException(String message){
            super(message);
        }
    }
}