package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
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
        super(0, 0, 0, 0);
        this.textHeight = textHeight;
        setText(text);
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getTextViewBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        float textHeight = this.textHeight;
        if(fitText)textHeight = Math.min(textHeight, (height)/(strs.length+padding));
        for(int i = 0; i<strs.length; i++){
            String str = strs[i];
            if(centered){
                renderer.drawCenteredText(x, y+(padding/2+i)*textHeight, x+width, y+(padding/2+i+1)*textHeight, str);
            }else{
                renderer.drawText(x+padding/2*textHeight, y+(padding/2+i)*textHeight, x+width, y+(padding/2+i+1)*textHeight, str);
            }
        }
    }
    public void setText(String text){
        Renderer renderer = new Renderer();
        strs = text.split("\n", -1);
        if(!fitText){
            for(String line : strs){
                width = Math.max(width, renderer.getStringWidth(line, textHeight));
                height+=textHeight;
            }
            width+=textHeight*padding;
            height+=textHeight*padding;
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