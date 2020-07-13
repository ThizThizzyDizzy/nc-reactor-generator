package planner.menu;
import generator.MultiblockGenerator;
import multiblock.Block;
import multiblock.Multiblock;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuGenerator extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMinimalistButton settings = add(new MenuComponentMinimalistButton(0, 0, 0, 50, "Settings", true, true));
    private final MenuComponentMinimalistButton output = add(new MenuComponentMinimalistButton(0, 0, 0, 50, "Output", true, true));
    private final MenuComponentMinimaList generators = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private Tab tab = Tab.SETTINGS;
    public MenuGenerator(GUI gui, Menu parent, Multiblock<Block> multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        settings.addActionListener((e) -> {
            settings.enabled = false;
            output.enabled = true;
            tab = Tab.SETTINGS;
        });
        output.addActionListener((e) -> {
            settings.enabled = true;
            output.enabled = false;
            tab = Tab.OUTPUT;
        });
    }
    @Override
    public void onGUIOpened(){
        if(MultiblockGenerator.getGenerators(multiblock).isEmpty()){
            gui.open(parent);
            Sys.error(ErrorLevel.severe, "No generators available for "+multiblock.getDefinitionName()+"!", null, ErrorCategory.bug);
        }
    }
    @Override
    public void renderBackground(){
        for(MenuComponent m : components){
            m.x = m.y = m.width = m.height = -1;
        }
        settings.width = output.width = Display.getWidth()/2;
        output.x = settings.width;
        output.y = settings.y = 0;
        output.height = settings.height = 50;
        switch(tab){
            case SETTINGS:
                generators.x = 0;
                generators.y = settings.height;
                generators.width = Display.getWidth()*3/4;
                generators.height = Display.getHeight()-generators.y;
                break;
            case OUTPUT:
                break;
        }
    }
    private static enum Tab{//I don't know why I made an enum for this, but here it is
        SETTINGS,OUTPUT;
    }
}