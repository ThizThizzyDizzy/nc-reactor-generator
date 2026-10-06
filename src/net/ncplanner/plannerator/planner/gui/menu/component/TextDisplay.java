package net.ncplanner.plannerator.planner.gui.menu.component;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.ui.component.Component;
import net.ncplanner.plannerator.planner.Core;
import org.joml.Vector2f;
public class TextDisplay extends Component{
    public float padding = 1;
    private String[] strs;
    private final float textHeight;
    private boolean centered;
    private boolean fitText = false;
    public TextDisplay(){
        this("");
    }
    public TextDisplay(float textHeight){
        this("", textHeight);
    }
    public TextDisplay(String text){
        this(text, 20);
    }
    public TextDisplay(String text, float textHeight){
        this(text, textHeight, false);
    }
    public TextDisplay(String text, boolean centered){
        this(text, 20, centered);
    }
    public TextDisplay(String text, float textHeight, boolean centered){
        this.textHeight = textHeight;
        setText(text);
    }
    @Override
    public void draw(double deltaTime){
        Renderer.setColor(Core.theme.getTextViewBackgroundColor());
        Renderer.fillRect(x, y, x+getWidth(), y+getHeight());
        Renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        float textHeight = this.textHeight;
        if(fitText)textHeight = Math.min(textHeight, (getHeight())/(strs.length+padding));
        for(int i = 0; i<strs.length; i++){
            String str = strs[i];
            if(centered){
                Renderer.drawCenteredText(x, y+(padding/2+i)*textHeight, x+getWidth(), y+(padding/2+i+1)*textHeight, str);
            }else{
                Renderer.drawText(x+padding/2*textHeight, y+(padding/2+i)*textHeight, x+getWidth(), y+(padding/2+i+1)*textHeight, str);
            }
        }
    }
    public void setText(String text){
        Renderer renderer = new Renderer();
        strs = text.split("\n", -1);
        Vector2f size = getSize();
        if(!fitText){
            for(String line : strs){
                size.x = Math.max(size.x, Renderer.getStringWidth(line, textHeight));
                size.y+=textHeight;
            }
            size.x+=textHeight*padding;
            size.y+=textHeight*padding;
        }
    }
    public void addText(String text){
        String txt = "";
        for(String s : strs){
            txt+="\n"+s;
        }
        setText(txt.substring(1)+text);
    }
    public TextDisplay fitText(){
        fitText = true;
        return this;
    }
}