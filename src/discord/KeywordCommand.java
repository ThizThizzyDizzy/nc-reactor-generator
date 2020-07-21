package discord;
import discord.keyword.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public abstract class KeywordCommand extends Command{
    private static HashMap<String, Keyword> keywords = new HashMap<>();
    private static ArrayList<String> keywordOrder = new ArrayList<>();
    static{
        addKeyword(new KeywordCuboid());
        addKeyword(new KeywordCube());
        addKeyword(new KeywordFuel());
        addKeyword(new KeywordUnderhaul());
        addKeyword(new KeywordOverhaul());
        addKeyword(new KeywordSymmetry());
        addKeyword(new KeywordConfiguration());
        addKeyword(new KeywordPriority());
    }
    public KeywordCommand(String command){
        super(command);
    }
    public static void addKeyword(Keyword keyword){
        String regex = keyword.getRegex();
        keywords.put(regex, keyword);
        keywordOrder.add(regex);
    }
    @Override
    public final void run(GuildMessageReceivedEvent event, String args, boolean debug){
        ArrayList<Object> debugText = new ArrayList<>();
        ArrayList<Keyword> words = new ArrayList<>();
        String str = args;
        for(String regex : keywordOrder){
            Pattern p = Pattern.compile("(^|\\s|,)"+regex+"(\\s|$|,)");
            Matcher m = p.matcher(args);
            int end = 0;
            while(m.find(end)){
                end = m.end()-1;
                Keyword key = keywords.get(regex).newInstance();
                String original = m.group().trim();
                if(!key.read(original))continue;
                words.add(key);
//                debugText.add(Core.theme.getRGB(.5f, .5f, .5f));
//                debugText.add(str.substring(0, str.indexOf(original)));
//                debugText.add(Core.theme.getRGB(key.getColor()));
//                debugText.add(original);
            }
            Keyword k = keywords.get(regex).newInstance();
            Color c = k.getColor();
            str = str.replaceAll("(?<!@)"+regex, "@@@@@"+c.getRGB()+"@@@$0@@@@@");
        }
        String[] strs = str.split("@@@@@");
        for(String s : strs){
            if(s.contains("@@@")){
                debugText.add(Core.theme.getRGB(new Color(Integer.parseInt(s.split("@@@")[0]))));
                debugText.add(s.split("@@@")[1]);
            }else{
                debugText.add(Core.theme.getRGB(.5f, .5f, .5f));
                debugText.add(s);
            }
        }
        if(debug){
            int border = 5;
            int textHeight = 20;
            int width = (int)FontManager.getLengthForStringWithHeight(args, textHeight)+1;
            BufferedImage image = Bot.makeImage(width+border*2, textHeight+border*2, (buff) -> {
                Core.applyColor(Core.theme.getEditorListBorderColor());
                Renderer2D.drawRect(0, 0, buff.width, buff.height, 0);
                double x = 5;
                for(Object o : debugText){
                    if(o instanceof Color){
                        Core.applyColor((Color)o);
                    }else if(o instanceof String){
                        String s = (String)o;
                        double len = FontManager.getLengthForStringWithHeight(s, textHeight);
                        Renderer2D.drawText(x, border, width+border, border+textHeight, s);
                        x+=len;
                    }
                }
            });
            File debugFile = new File("debug.png");
            try{
                ImageIO.write(image, "png", debugFile);
            }catch(IOException ex){
                Logger.getLogger(KeywordCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            event.getChannel().sendFile(debugFile, "debug.png").queue();
        }
        run(event, words, debug);
    }
    public void run(GuildMessageReceivedEvent event, ArrayList<Keyword> keywords, boolean debug){
        
    }
}