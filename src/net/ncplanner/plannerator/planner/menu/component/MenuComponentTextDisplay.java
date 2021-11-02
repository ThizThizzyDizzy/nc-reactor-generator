package net.ncplanner.plannerator.planner.menu.component;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTextDisplay extends MenuComponent{
    public double padding = 1;
    private String[] strs;
    private final double textHeight;
    private boolean centered;
    public MenuComponentTextDisplay(String text){
        this(text, 20, false);
    }
    public MenuComponentTextDisplay(String text, double textHeight){
        this(text, textHeight, false);
    }
    public MenuComponentTextDisplay(String text, boolean centered){
        this(text, 20, false);
    }
    public MenuComponentTextDisplay(String text, double textHeight, boolean centered){
        super(0, 0, 0, 0);
        this.textHeight = textHeight;
        setText(text);
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getTextViewBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        for(int i = 0; i<strs.length; i++){
            String str = strs[i];
            if(centered){
                drawCenteredText(x, y+(padding/2+i)*textHeight, x+width, y+(padding/2+i+1)*textHeight, str);
            }else{
                drawText(x+padding/2*textHeight, y+(padding/2+i)*textHeight, x+width, y+(padding/2+i+1)*textHeight, str);
            }
        }
    }
    public void setText(String text){
        strs = text.split("\n", -1);
        for(String line : strs){
            width = Math.max(width, FontManager.getLengthForStringWithHeight(line, textHeight));
            height+=textHeight;
        }
        width+=textHeight*padding;
        height+=textHeight*padding;
    }
    public void addText(String text){
        String txt = "";
        for(String s : strs){
            txt+="\n"+s;
        }
        setText(txt.substring(1)+text);
    }
}