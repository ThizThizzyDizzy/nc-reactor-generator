package discord;
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
import net.dv8tion.jda.api.entities.MessageChannel;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public abstract class KeywordCommand extends Command{
    private HashMap<String, Keyword> keywords = new HashMap<>();
    private ArrayList<String> keywordOrder = new ArrayList<>();
    public KeywordCommand(String command, String... alternates){
        super(command, alternates);
        addKeywords();
    }
    public abstract void addKeywords();
    public void addKeyword(Keyword keyword){
        String regex = keyword.getRegex();
        keywords.put(regex, keyword);
        keywordOrder.add(regex);
    }
    @Override
    public final void run(net.dv8tion.jda.api.entities.User user, MessageChannel channel, String args, boolean debug){
        ArrayList<Keyword> words = new ArrayList<>();
        String str = args.toLowerCase();
        for(String regex : keywordOrder){
            Pattern p = Pattern.compile("(^|\\s|,)"+regex+"(\\s|$|,)");
            String theArgs = (keywords.get(regex).caseSensitive()?args:args.toLowerCase());
            Matcher m = p.matcher(theArgs);
            int end = 0;
            while(m.find(end)){
                end = m.end()-1;
                Keyword key = keywords.get(regex).newInstance();
                String original = m.group().trim();
                if(!key.read(original))continue;
                words.add(key);
            }
            Keyword k = keywords.get(regex).newInstance();
            Color c = k.getColor();
            str = str.replaceAll("(?<!@)"+regex, "@@@@@"+c.getRGB()+"@@@$0@@@@@");
        }
        ArrayList<Object> debugText = new ArrayList<>();
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
        int border = 5;
        int textHeight = 20;
        int wide = (int)FontManager.getLengthForStringWithHeight(args, textHeight)+1;
        for(Keyword w : words){
            wide = Math.max(wide, (int)FontManager.getLengthForStringWithHeight(w.name+" | "+w.input, textHeight)+1);
        }
        int width = wide;
        BufferedImage image = Bot.makeImage(width+border*2, textHeight*(1+words.size())+border*2, (buff) -> {
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
            for(int i = 0; i<words.size(); i++){
                Keyword word = words.get(i);
                Core.applyColor(Core.theme.getRGB(word.getColor()));
                Renderer2D.drawText(border, border+(i+1)*textHeight, width+border, border+(i+2)*textHeight, word.name+" | "+word.input);
            }
        });
        File debugFile = new File("debug.png");
        try{
            ImageIO.write(image, "png", debugFile);
        }catch(IOException ex){
            Logger.getLogger(KeywordCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        channel.sendFile(debugFile, "debug.png").complete();
        debugFile.delete();
        run(channel, words, debug);
    }
    public abstract void run(MessageChannel channel, ArrayList<Keyword> keywords, boolean debug);
}