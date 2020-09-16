package planner.menu.component;
import planner.Core;
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
        strs = text.split("\n");
        for(String line : strs){
            width = Math.max(width, FontManager.getLengthForStringWithHeight(line, textHeight));
            height+=textHeight;
        }
        width+=textHeight*padding;
        height+=textHeight*padding;
    }
    @Override
    public void render(){
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
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
        strs = text.split("\n");
        for(String line : strs){
            width = Math.max(width, FontManager.getLengthForStringWithHeight(line, textHeight));
            height+=textHeight;
        }
        width+=textHeight*padding;
        height+=textHeight*padding;
    }
}