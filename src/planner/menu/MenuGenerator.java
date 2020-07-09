package planner.menu;
import generator.MultiblockGenerator;
import multiblock.Block;
import multiblock.Multiblock;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuGenerator extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMinimalistButton settings = new MenuComponentMinimalistButton(0, 0, 0, 50, "Settings", true, true);
    private final MenuComponentMinimalistButton output = new MenuComponentMinimalistButton(0, 0, 0, 50, "Output", true, true);
    private Tab tab;
    public MenuGenerator(GUI gui, Menu parent, Multiblock<Block> multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        settings.addActionListener((e) -> {
            settings.enabled = false;
            output.enabled = true;
            tab = Tab.SETTINGS;
        });
        output.addActionListener((e) -> {
            settings.enabled = false;
            output.enabled = true;
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
        settings.width = output.width = Display.getWidth()/2;
        output.x = settings.width;
    }
    private static enum Tab{//I don't know why I made an enum for this, but here it is
        SETTINGS,OUTPUT;
    }
}