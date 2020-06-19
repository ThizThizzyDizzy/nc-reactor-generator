package planner.menu.component;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuComponentMinimalistTextBox extends MenuComponentTextBox{
    private static final int NONE = 0;
    private static final int INT = 1;
    private static final int FLOAT = 2;
    private int filter = NONE;
    public MenuComponentMinimalistTextBox(double x, double y, double width, double height, String text, boolean editable){
        super(x, y, width, height, text, editable);
        setColor(new Color(.25f, .25f, .5f, 1f));
        setForegroundColor(new Color(.1f, .1f, .2f, 1f));
    }
    @Override
    public void render(){
        if(textInset<0){
            textInset = height/10;
        }
        Color dark = color.darker();
        GL11.glColor4f(dark.getRed()/255F, dark.getGreen()/255F, dark.getBlue()/255F, dark.getAlpha()/255F);
        drawRect(x, y, x+width, y+height, 0);
        GL11.glColor4f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F, color.getAlpha()/255F);
        drawRect(x+textInset/2, y+textInset/2, x+width-textInset/2, y+height-textInset/2, 0);
        GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255F);
        if(editable){
            drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+(((gui.tick&20)<10&&isSelected)?"_":""));
        }else{
            drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text);
        }
    }
    @Override
    public void processKeyboard(char character, int key, boolean pressed, boolean repeat){
        String lastText = text;
        super.processKeyboard(character, key, pressed, repeat);
        if(filter==INT){
            try{
                Integer.parseInt(text);
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
        if(filter==FLOAT){
            try{
                Float.parseFloat(text);
            }catch(NumberFormatException ex){
                text = lastText;
            }
        }
    }
    public MenuComponentMinimalistTextBox setIntFilter(){
        filter = INT;
        return this;
    }
    public MenuComponentMinimalistTextBox setFloatFilter(){
        filter = FLOAT;
        return this;
    }
}