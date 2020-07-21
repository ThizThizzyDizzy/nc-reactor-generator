package discord;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import planner.Core;
import planner.Core.BufferRenderer;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
public class Bot extends ListenerAdapter{
    public static boolean debug = false;
    private static ArrayList<String> prefixes = new ArrayList<>();
    private static ArrayList<Long> botChannels = new ArrayList<>();
    private static ArrayList<Long> dataChannels = new ArrayList<>();
    private static Config config;
    private static int cookies;
    private static JDA jda;
    private static final ArrayList<Command> commands = new ArrayList<>();
    static{
        commands.add(new Command("debug"){
            @Override
            public String getHelpText(){
                return "Toggles Debug Mode";
            }
            @Override
            public void run(GuildMessageReceivedEvent event, String args, boolean debug){
                Bot.debug = !Bot.debug;
                event.getChannel().sendMessage("Debug mode **"+(Bot.debug?"Enabled":"Disabled")+"**").queue();
            }
        });
        commands.add(new Command("help"){
            @Override
            public String getHelpText(){
                return "Shows this help window";
            }
            @Override
            public void run(GuildMessageReceivedEvent event, String args, boolean debug){
                EmbedBuilder builder = createEmbed(jda.getSelfUser().getName()+" Help");
                String prefx = "";
                for(String s : Bot.prefixes){
                    prefx+="`"+s+"`\t";
                }
                builder.addField("Prefixes", prefx.trim(), false);
                for(Command c : commands){
                    if(c.isSecret())continue;
                    builder.addField(prefixes.get(0)+c.command, c.getHelpText(), false);
                }
                event.getChannel().sendMessage(builder.build()).queue();
            }
        });
        commands.add(new KeywordCommand("generate"){
            @Override
            public String getHelpText(){
                return "¯\\_(ツ)_/¯";
            }
        });
    }
    private static EmbedBuilder createEmbed(String title){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setColor(new Color(255, 200, 0));
        builder.setFooter("Powered by https://github.com/ThizThizzyDizzy/nc-reactor-generator/releases");
        return builder;
    }
    public static void start(String[] args){
        for(int i = 2; i<args.length; i++){
            String arg = args[i];
            if(arg.startsWith("bot"))botChannels.add(Long.parseLong(arg.substring(3)));
            else if(arg.startsWith("data"))dataChannels.add(Long.parseLong(arg.substring(4)));
            else prefixes.add(arg);
        }
        if(prefixes.isEmpty())prefixes.add("-");
        config = Config.newConfig(new File(new File("special.dat").getAbsolutePath()));
        config.load();
        cookies = config.get("cookies", 0);
        JDABuilder b = new JDABuilder(AccountType.BOT);
        b.setToken(args[1]);
        b.setActivity(Activity.watching("cookies accumulate ("+cookies+")"));
        b.addEventListeners(new Bot());
        try{
            jda = b.build();
        }catch(LoginException ex){
            Sys.error(ErrorLevel.critical, "Failed to log in!", ex, ErrorCategory.InternetIO);
        }
    }
    public static void stop(){
        if(jda!=null)jda.shutdownNow();
    }
    public static void render2D(){
        if(pendingImage!=null){
            image = Core.makeImage(imgWidth, imgHeight, pendingImage);
            pendingImage = null;
        }
    }
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot())return;
        if(!botChannels.contains(event.getChannel().getIdLong()))return;
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
            for(Command cmd : commands){
                if(command.equals(cmd.command)||command.startsWith(cmd.command+" ")){
                    String args = command.substring(cmd.command.length()).trim();
                    try{
                        cmd.run(event, args, debug);
                    }catch(Exception ex){
                        printErrorMessage(event.getChannel(), "Caught exception running command `"+cmd.command+"`!", ex);
                    }
                }
            }
        }catch(Exception ex){
            printErrorMessage(event.getChannel(), "Caught exception loading command!", ex);
        }
    }
    private void printErrorMessage(TextChannel channel, String message, Exception ex){
        String trace = "";
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for(StackTraceElement e : stackTrace){
            if(e.getClassName().startsWith("net."))continue;
            if(e.getClassName().startsWith("com."))continue;
            String[] splitClassName = e.getClassName().split("\\Q.");
            String filename = splitClassName[splitClassName.length-1]+".java";
            String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
            if((trace+nextLine).length()+4>1024){
                trace+="\n...";
                break;
            }else trace+=nextLine;
        }
        channel.sendMessage(createEmbed(message).addField(ex.getClass().getName()+": "+ex.getMessage(), trace.trim(), false).build()).queue();
    }
    private static int imgWidth, imgHeight;
    private static BufferRenderer pendingImage = null;
    private static BufferedImage image = null;
    public static BufferedImage makeImage(int width, int height, BufferRenderer r){
        imgWidth = width;
        imgHeight = height;
        pendingImage = r;
        image = null;
        while(image==null)
            try{
                Thread.sleep(10);
            }catch(InterruptedException ex){}
        return image;
    }
}