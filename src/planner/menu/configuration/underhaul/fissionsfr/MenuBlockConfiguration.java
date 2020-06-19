package planner.menu.configuration.underhaul.fissionsfr;
import java.awt.Color;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.configuration.underhaul.fissionsfr.Block;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockConfiguration extends Menu{
    private static final Color textColor = new Color(.1f, .1f, .2f, 1f);
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistTextBox cooling = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton fuelCell = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Fuel Cell", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton moderator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Moderator", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Block block;
    public MenuBlockConfiguration(GUI gui, Menu parent, Block block){
        super(gui, parent);
        rules.addActionListener((e) -> {
            gui.open(new MenuPlacementRulesConfiguration(gui, this, block));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        name.text = block.name;
        cooling.text = block.cooling+"";
        fuelCell.setIndex(block.fuelCell?1:0);
        moderator.setIndex(block.moderator?1:0);
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.cooling = Integer.parseInt(cooling.text);
        block.fuelCell = fuelCell.getIndex()==1;
        block.moderator = moderator.getIndex()==1;
        super.onGUIClosed();
    }
    @Override
    public void render(int millisSinceLastTick){
        cooling.width = Display.getWidth()*.75;
        cooling.x = Display.getWidth()-cooling.width;
        moderator.width = fuelCell.width = name.width = rules.width = back.width = Display.getWidth();
        moderator.height = fuelCell.height = cooling.height = name.height = rules.height = back.height = Display.getHeight()/16;
        cooling.y = name.height;
        fuelCell.y = cooling.y+cooling.height;
        moderator.y = fuelCell.y+fuelCell.height;
        rules.y = moderator.y+moderator.height;
        back.y = Display.getHeight()-back.height;
        GL11.glColor4f(textColor.getRed()/255f, textColor.getGreen()/255f, textColor.getBlue()/255f, textColor.getAlpha()/255f);
        drawText(0, Display.getHeight()/16, Display.getWidth()*.25, Display.getHeight()/8, "Cooling");
        GL11.glColor4f(1, 1, 1, 1);
        super.render(millisSinceLastTick);
    }
}