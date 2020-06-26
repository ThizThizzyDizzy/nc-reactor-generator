package planner.menu.configuration.underhaul.fissionsfr;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.Block;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistTextBox cooling = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton fuelCell = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Fuel Cell", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton moderator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Moderator", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistTextBox active = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true));
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
        active.text = block.active==null?"":block.active;
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.cooling = Integer.parseInt(cooling.text);
        block.fuelCell = fuelCell.getIndex()==1;
        block.moderator = moderator.getIndex()==1;
        block.active = active.text.trim().isEmpty()?null:active.text;
        super.onGUIClosed();
    }
    @Override
    public void render(int millisSinceLastTick){
        active.width = cooling.width = Display.getWidth()*.75;
        active.x = cooling.x = Display.getWidth()-cooling.width;
        moderator.width = fuelCell.width = name.width = rules.width = back.width = Display.getWidth();
        active.height = moderator.height = fuelCell.height = cooling.height = name.height = rules.height = back.height = Display.getHeight()/16;
        cooling.y = name.height;
        fuelCell.y = cooling.y+cooling.height;
        moderator.y = fuelCell.y+fuelCell.height;
        active.y = moderator.y+moderator.height;
        rules.y = active.y+active.height;
        back.y = Display.getHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Display.getHeight()/16, Display.getWidth()*.25, Display.getHeight()/8, "Cooling");
        drawText(0, Display.getHeight()/16*4, Display.getWidth()*.25, Display.getHeight()/16*5, "Active");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}