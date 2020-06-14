package planner.menu;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuComponentMinimalistTextBox extends MenuComponentTextBox{
    public MenuComponentMinimalistTextBox(double x, double y, double width, double height, String text, boolean editable){
        super(x, y, width, height, text, editable);
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
}