package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.underhaul.fissionsfr.Block;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBlocksConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Block", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuBlocksConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            Block b = new Block("New Block");
            configuration.underhaul.fissionSFR.blocks.add(b);
            Core.configuration.underhaul.fissionSFR.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Block b : configuration.underhaul.fissionSFR.blocks){
            list.add(new MenuComponentBlockConfiguration(b));
        }
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
    public void render(int millisSinceLastTick){
        list.width = Core.helper.displayWidth();
        list.height = Core.helper.displayHeight()-back.height-add.height;
        for(simplelibrary.opengl.gui.components.MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = Core.helper.displayWidth();
        add.height = back.height = Core.helper.displayHeight()/16;
        back.y = Core.helper.displayHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : list.components){
            if(c instanceof MenuComponentBlockConfiguration){
                if(button==((MenuComponentBlockConfiguration) c).delete){
                    configuration.underhaul.fissionSFR.blocks.remove(((MenuComponentBlockConfiguration) c).block);
                    Core.configuration.underhaul.fissionSFR.allBlocks.remove(((MenuComponentBlockConfiguration) c).block);
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