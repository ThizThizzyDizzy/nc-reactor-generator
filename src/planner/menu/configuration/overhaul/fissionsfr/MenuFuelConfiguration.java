package planner.menu.configuration.overhaul.fissionsfr;
import java.awt.Color;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.configuration.overhaul.fissionsfr.Fuel;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFuelConfiguration extends Menu{
    private static final Color textColor = new Color(.1f, .1f, .2f, 1f);
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox heat = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox time = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox criticality = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton selfPriming = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Self Prming", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Fuel fuel;
    public MenuFuelConfiguration(GUI gui, Menu parent, Fuel fuel){
        super(gui, parent);
        back.addActionListener((e) -> {
            fuel.name = name.text;
            fuel.efficiency = Float.parseFloat(efficiency.text);
            fuel.heat = Integer.parseInt(heat.text);
            fuel.time = Integer.parseInt(time.text);
            fuel.criticality = Integer.parseInt(criticality.text);
            fuel.selfPriming = selfPriming.getIndex()==1;
            gui.open(parent);
        });
        this.fuel = fuel;
    }
    @Override
    public void onGUIOpened(){
        name.text = fuel.name;
        efficiency.text = fuel.efficiency+"";
        heat.text = fuel.heat+"";
        time.text = fuel.time+"";
        criticality.text = fuel.criticality+"";
        selfPriming.setIndex(fuel.selfPriming?1:0);
    }
    @Override
    public void render(int millisSinceLastTick){
        back.width = name.width = selfPriming.width = Display.getWidth();
        efficiency.width = heat.width = time.width = criticality.width = Display.getWidth()*.75;
        efficiency.x = heat.x = time.x = criticality.x = Display.getWidth()*.25;
        efficiency.height = heat.height = time.height = criticality.height = selfPriming.height = name.height = back.height = Display.getHeight()/16;
        efficiency.y = name.height;
        heat.y = efficiency.y+efficiency.height;
        time.y = heat.y+heat.height;
        criticality.y = time.y+time.height;
        selfPriming.y = criticality.y+criticality.height;
        back.y = Display.getHeight()-back.height;
        GL11.glColor4f(textColor.getRed()/255f, textColor.getGreen()/255f, textColor.getBlue()/255f, textColor.getAlpha()/255f);
        drawText(0, Display.getHeight()/16, Display.getWidth()*.25, Display.getHeight()/16*2, "Power");
        drawText(0, Display.getHeight()/16*2, Display.getWidth()*.25, Display.getHeight()/16*3, "Heat");
        drawText(0, Display.getHeight()/16*3, Display.getWidth()*.25, Display.getHeight()/16*4, "Time");
        drawText(0, Display.getHeight()/16*4, Display.getWidth()*.25, Display.getHeight()/16*5, "Criticality");
        GL11.glColor4f(1, 1, 1, 1);
        super.render(millisSinceLastTick);
    }
}