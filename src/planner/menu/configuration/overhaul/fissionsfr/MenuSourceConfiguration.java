package planner.menu.configuration.overhaul.fissionsfr;
import java.awt.Color;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.configuration.overhaul.fissionsfr.Source;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuSourceConfiguration extends Menu{
    private static final Color textColor = new Color(.1f, .1f, .2f, 1f);
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Source source;
    public MenuSourceConfiguration(GUI gui, Menu parent, Source source){
        super(gui, parent);
        back.addActionListener((e) -> {
            source.name = name.text;
            source.efficiency = Float.parseFloat(efficiency.text);
            gui.open(parent);
        });
        this.source = source;
    }
    @Override
    public void onGUIOpened(){
        name.text = source.name;
        efficiency.text = source.efficiency+"";
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = Display.getWidth();
        efficiency.width = Display.getWidth()*.75;
        efficiency.x = Display.getWidth()*.25;
        efficiency.height = name.height = back.height = Display.getHeight()/16;
        efficiency.y = name.height;
        back.y = Display.getHeight()-back.height;
        GL11.glColor4f(textColor.getRed()/255f, textColor.getGreen()/255f, textColor.getBlue()/255f, textColor.getAlpha()/255f);
        drawText(0, Display.getHeight()/16, Display.getWidth()*.25, Display.getHeight()/16*2, "Efficiency");
        GL11.glColor4f(1, 1, 1, 1);
        super.render(millisSinceLastTick);
    }
}