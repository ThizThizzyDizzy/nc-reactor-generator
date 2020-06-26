package planner.menu.component;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import planner.configuration.underhaul.fissionsfr.Fuel;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentUnderFuel extends MenuComponent{
    private final Fuel fuel;
    public MenuComponentUnderFuel(Fuel fuel){
        super(0, 0, 0, 0);
        float tint = .9f;
        color = new Color(tint/2, tint/2, tint, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
        this.fuel = fuel;
    }
    @Override
    public void render(){
        Color col = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        if(isSelected)col = col.brighter();
        if(isMouseOver)col = col.brighter();
        GL11.glColor4f(col.getRed()/255F, col.getGreen()/255F, col.getBlue()/255F, col.getAlpha()/255F);
        drawRect(x, y, x+width, y+height, 0);
        GL11.glColor4d(foregroundColor.getRed()/255f, foregroundColor.getGreen()/255f, foregroundColor.getBlue()/255f, foregroundColor.getAlpha()/255f);
        drawText();
    }
    public void drawText(){
        drawCenteredText(x, y+height*.2, x+width, y+height*.8, fuel.name);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}