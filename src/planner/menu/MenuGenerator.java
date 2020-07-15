package planner.menu;
import generator.MultiblockGenerator;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.Multiblock;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMultiblockGenerator;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentToggleBlock;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuGenerator extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMinimalistButton settings = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Settings", false, true));
    private final MenuComponentMinimalistButton output = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Generate", true, true));
    private final MenuComponentMulticolumnMinimaList blocks = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 64, 64, 32));
    private final MenuComponentMinimaList generators = add(new MenuComponentMinimaList(0, 0, 0, 0, 32));
    private final MenuComponentMinimaList generatorSettings = add(new MenuComponentMinimaList(0, 0, 0, 0, 32));
    private final MenuComponentMinimaList multiblockSettings = add(new MenuComponentMinimaList(0, 0, 0, 0, 32));
    private final MenuComponentLabel blocksHeader = add(new MenuComponentLabel(0, 0, 0, 0, "Blocks", true));
    private final MenuComponentLabel generatorsHeader = add(new MenuComponentLabel(0, 0, 0, 0, "Generators", true));
    private final MenuComponentLabel settingsHeader = add(new MenuComponentLabel(0, 0, 0, 0, "Settings", true));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    private Tab tab = Tab.SETTINGS;
    private final ArrayList<MultiblockGenerator> multiblockGenerators;
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
        done.addActionListener((e) -> {
            gui.open(parent);
        });
        multiblockGenerators = MultiblockGenerator.getGenerators(multiblock);
        for(MultiblockGenerator gen : multiblockGenerators){
            generators.add(new MenuComponentMultiblockGenerator(gen));
        }
        if(!multiblockGenerators.isEmpty())generators.setSelectedIndex(0);
    }
    @Override
    public void onGUIOpened(){
        if(multiblockGenerators.isEmpty()){
            gui.open(parent);
            Sys.error(ErrorLevel.severe, "No generators available for "+multiblock.getDefinitionName()+"!", null, ErrorCategory.bug);
        }
        blocks.components.clear();
        for(Block b : multiblock.getAvailableBlocks()){
            blocks.add(new MenuComponentToggleBlock(b));
        }
        generatorSettings.components.clear();
        getGenerator().addSettings(generatorSettings, multiblock);
        multiblockSettings.components.clear();
        multiblock.addGeneratorSettings(multiblockSettings);
    }
    @Override
    public void renderBackground(){
        for(MenuComponent m : components){
            m.x = m.y = m.width = m.height = -1;
        }
        settings.width = output.width = Display.getWidth()/2;
        output.x = settings.width;
        output.y = settings.y = 0;
        output.height = settings.height = done.height = 64;
        done.x = 0;
        done.y = Display.getHeight()-done.height;
        done.width = Display.getWidth();
        switch(tab){
            case SETTINGS:
                blocksHeader.x = 0;
                blocksHeader.y = settings.height;
                blocksHeader.width = blocks.width = Display.getWidth()/4;
                blocksHeader.height = settings.height;
                blocks.x = 0;
                blocks.y = settings.height*2;
                blocks.height = Display.getHeight()-blocks.y-done.height;
                generatorsHeader.x = Display.getWidth()/4;
                generatorsHeader.y = settings.height;
                generatorsHeader.width = generators.width = Display.getWidth()/4;
                generatorsHeader.height = settings.height;
                generators.x = Display.getWidth()/4;
                generators.y = settings.height*2;
                generators.width = Display.getWidth()/4;
                generators.height = Display.getHeight()-generators.y-done.height;
                for(MenuComponent c : generators.components){
                    c.width = generators.width-(generators.hasVertScrollbar()?generators.vertScrollbarWidth:0);
                }
                settingsHeader.x = Display.getWidth()/2;
                settingsHeader.y = settings.height;
                settingsHeader.width = Display.getWidth()/2;
                settingsHeader.height = settings.height;
                generatorSettings.x = Display.getWidth()/2;
                generatorSettings.width = Display.getWidth()/4;
                generatorSettings.y = settings.height*2;
                generatorSettings.width = Display.getWidth()/4;
                generatorSettings.height = Display.getHeight()-generatorSettings.y-done.height;
                for(MenuComponent c : generatorSettings.components){
                    c.width = generatorSettings.width-(generatorSettings.hasVertScrollbar()?generatorSettings.vertScrollbarWidth:0);
                }
                multiblockSettings.x = Display.getWidth()*3/4;
                multiblockSettings.y = settings.height*2;
                multiblockSettings.width = Display.getWidth()/4;
                multiblockSettings.height = Display.getHeight()-multiblockSettings.y-done.height;
                for(MenuComponent c : multiblockSettings.components){
                    c.width = multiblockSettings.width-(multiblockSettings.hasVertScrollbar()?multiblockSettings.vertScrollbarWidth:0);
                }
                break;
            case OUTPUT:
                break;
        }
    }
    private MultiblockGenerator getGenerator(){
        return multiblockGenerators.get(generators.getSelectedIndex());
    }
    private static enum Tab{//I don't know why I made an enum for this, but here it is
        SETTINGS,OUTPUT;
    }
}