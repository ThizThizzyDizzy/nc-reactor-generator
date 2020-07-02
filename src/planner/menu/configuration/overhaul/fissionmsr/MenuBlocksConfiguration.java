package planner.menu.configuration.overhaul.fissionmsr;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.configuration.overhaul.fissionmsr.Block;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBlocksConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Block", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    public MenuBlocksConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        add.addActionListener((e) -> {
            Block b = new Block("New Block");
            Core.configuration.overhaul.fissionMSR.blocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Block b : Core.configuration.overhaul.fissionMSR.blocks){
            list.add(new MenuComponentBlockConfiguration(b));
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        list.width = Display.getWidth();
        list.height = Display.getHeight()-back.height-add.height;
        for(MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = Display.getWidth();
        add.height = back.height = Display.getHeight()/16;
        back.y = Display.getHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(MenuComponent c : list.components){
            if(c instanceof MenuComponentBlockConfiguration){
                if(button==((MenuComponentBlockConfiguration) c).delete){
                    Core.configuration.overhaul.fissionMSR.blocks.remove(((MenuComponentBlockConfiguration) c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlockConfiguration) c).edit){
                    gui.open(new MenuBlockConfiguration(gui, this, ((MenuComponentBlockConfiguration) c).block));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}