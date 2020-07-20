package planner.menu;
import generator.MultiblockGenerator;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.GenerateAction;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMultiblockDisplay;
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
    private final MenuComponentLabel threadsLabel = add(new MenuComponentLabel(0, 0, 0, 64, "No Threads", true));
    private final MenuComponentMinimalistButton addThread = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Add Thread", true, true));
    private final MenuComponentMinimalistButton removeThread = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Remove Thread", true, true));
    private final MenuComponentMinimaList multiblockLists = add(new MenuComponentMinimaList(0, 0, 0, 0, 64));
    private Tab tab = Tab.SETTINGS;
    private final ArrayList<MultiblockGenerator> multiblockGenerators;
    private MultiblockGenerator generator = null;
    private int lastIndex;
    private int threads = 0;
    public MenuGenerator(GUI gui, MenuEdit editor, Multiblock<Block> multiblock){
        super(gui, editor);
        this.multiblock = multiblock;
        settings.addActionListener((e) -> {
            settings.enabled = false;
            output.enabled = true;
            tab = Tab.SETTINGS;
        });
        output.addActionListener((e) -> {
            settings.enabled = true;
            output.enabled = false;
            tab = Tab.GENERATE;
            ArrayList<Block> allowedBlocks = new ArrayList<>();
            for(MenuComponent c : blocks.components){
                if(((MenuComponentToggleBlock)c).enabled)allowedBlocks.add(((MenuComponentToggleBlock)c).block);
            }
            generator.refreshSettings(allowedBlocks);
            if(threads<=0){
                generator.importMultiblock(multiblock);
                threads = 1;
            }
            threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
        });
        done.addActionListener((e) -> {
            if(generator!=null)generator.stopAllThreads();
            Multiblock generated = generator.getMainMultiblock();
            if(generated!=null)editor.multiblock.action(new GenerateAction(generated));
            gui.open(editor);
        });
        addThread.addActionListener((e) -> {
            threads++;
            threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
        });
        removeThread.addActionListener((e) -> {
            threads--;
            if(threads<1)threads = 1;
            threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
        });
        multiblockGenerators = MultiblockGenerator.getGenerators(multiblock);
        for(MultiblockGenerator gen : multiblockGenerators){
            generators.add(new MenuComponentMultiblockGenerator(gen));
        }
        if(!multiblockGenerators.isEmpty())generators.setSelectedIndex(0);
        generator = multiblockGenerators.get(generators.getSelectedIndex()).newInstance(multiblock);
        lastIndex = generators.getSelectedIndex();
    }
    @Override
    public void tick(){
        super.tick();
        if(generator!=null){
            if(generator.getActiveThreads()<threads){
                generator.startThread();
            }
            if(generator.getActiveThreads()>threads){
                generator.stopThread();
            }
            ArrayList<Multiblock>[] multiblocks = generator.getMultiblockLists();
            if(multiblocks.length!=multiblockLists.components.size()){
                multiblockLists.components.clear();
                for(ArrayList<Multiblock> mbs : multiblocks){
                    MenuComponentMulticolumnMinimaList lst = multiblockLists.add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 300, 800, 48));
                    for(Multiblock mb : mbs){
                        lst.add(new MenuComponentMultiblockDisplay(mb));
                    }
                }
            }else{
                for(int i = 0; i<multiblocks.length; i++){
                    ArrayList<Multiblock> mbs = multiblocks[i];
                    MenuComponentMulticolumnMinimaList lst = (MenuComponentMulticolumnMinimaList)multiblockLists.components.get(i);
                    if(mbs.size()!=lst.components.size()){
                        lst.components.clear();
                        for(Multiblock mb : mbs){
                            lst.add(new MenuComponentMultiblockDisplay(mb));
                        }
                    }else{
                        for(int j = 0; j<mbs.size(); j++){
                            ((MenuComponentMultiblockDisplay)lst.components.get(j)).multiblock = mbs.get(j);
                        }
                    }
                }
            }
        }
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
        generator.addSettings(generatorSettings, multiblock);
        multiblockSettings.components.clear();
        multiblock.addGeneratorSettings(multiblockSettings);
    }
    @Override
    public void renderBackground(){
        if(generators.getSelectedIndex()!=lastIndex){
            int idx = generators.getSelectedIndex();
            if(idx!=-1){
                generator = multiblockGenerators.get(generators.getSelectedIndex()).newInstance(multiblock);
                lastIndex = idx;
            }
        }
        for(MenuComponent m : components){
            m.x = m.y = m.width = m.height = -1;
        }
        settings.width = output.width = Display.getWidth()/3;
        settings.x = Display.getWidth()-settings.width-output.width;
        output.x = settings.x+settings.width;
        output.y = settings.y = 0;
        output.height = settings.height = 64;
        switch(tab){
            case SETTINGS:
                done.height = 64;
                done.x = 0;
                done.y = 0;
                done.width = Display.getWidth()/4;
                blocksHeader.x = 0;
                blocksHeader.y = settings.height;
                blocksHeader.width = blocks.width = Display.getWidth()/4;
                blocksHeader.height = settings.height;
                blocks.x = 0;
                blocks.y = settings.height*2;
                blocks.height = Display.getHeight()-blocks.y;
                generatorsHeader.x = Display.getWidth()/4;
                generatorsHeader.y = settings.height;
                generatorsHeader.width = generators.width = Display.getWidth()/4;
                generatorsHeader.height = settings.height;
                generators.x = Display.getWidth()/4;
                generators.y = settings.height*2;
                generators.width = Display.getWidth()/4;
                generators.height = Display.getHeight()-generators.y;
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
                generatorSettings.height = Display.getHeight()-generatorSettings.y;
                for(MenuComponent c : generatorSettings.components){
                    c.width = generatorSettings.width-(generatorSettings.hasVertScrollbar()?generatorSettings.vertScrollbarWidth:0);
                }
                multiblockSettings.x = Display.getWidth()*3/4;
                multiblockSettings.y = settings.height*2;
                multiblockSettings.width = Display.getWidth()/4;
                multiblockSettings.height = Display.getHeight()-multiblockSettings.y;
                for(MenuComponent c : multiblockSettings.components){
                    c.width = multiblockSettings.width-(multiblockSettings.hasVertScrollbar()?multiblockSettings.vertScrollbarWidth:0);
                }
                break;
            case GENERATE:
                threadsLabel.x = 0;
                threadsLabel.y = 64;
                threadsLabel.width = Display.getWidth()/3;
                threadsLabel.height = 48;
                addThread.x = threadsLabel.width;
                addThread.y = 64;
                addThread.width = Display.getWidth()/3;
                addThread.height = 48;
                removeThread.x = addThread.x+addThread.width;
                removeThread.y = 64;
                removeThread.width = Display.getWidth()-removeThread.x;
                removeThread.height = 48;
                multiblockLists.x = 0;
                multiblockLists.y = threadsLabel.height+settings.height;
                multiblockLists.width = Display.getWidth();
                multiblockLists.height = Display.getHeight()-multiblockLists.y;
                for(MenuComponent m : multiblockLists.components){
                    m.width = multiblockLists.width;
                    m.height = 800;
                }
                break;
        }
    }
    private static enum Tab{//I don't know why I made an enum for this, but here it is
        SETTINGS,GENERATE;
    }
}