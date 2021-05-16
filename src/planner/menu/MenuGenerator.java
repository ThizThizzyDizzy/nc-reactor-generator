package planner.menu;
import generator.MultiblockGenerator;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.Range;
import multiblock.action.GenerateAction;
import planner.exception.MissingConfigurationEntryException;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.generator.MenuComponentMultiblockDisplay;
import planner.menu.component.generator.MenuComponentMultiblockGenerator;
import planner.menu.component.generator.MenuComponentToggleBlock;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuGenerator extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMinimalistButton settings = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Settings", false, true).setTooltip("Modify generator settings\nThis does not stop the generator\nSettings are not applied until you click Generate"));
    private final MenuComponentMinimalistButton output = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Generate", true, true).setTooltip("Starts generating reactors or applies new settings"));
    private final MenuComponentMulticolumnMinimaList blocks = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 64, 64, 32));
    private final MenuComponentMinimaList generators = add(new MenuComponentMinimaList(0, 0, 0, 0, 32));
    private final MenuComponentMinimaList generatorSettings = add(new MenuComponentMinimaList(0, 0, 0, 0, 32));
    private final MenuComponentMinimaList multiblockSettings = add(new MenuComponentMinimaList(0, 0, 0, 0, 32));
    private final MenuComponentLabel blocksHeader = add(new MenuComponentLabel(0, 0, 0, 0, "Blocks", true));
    private final MenuComponentLabel generatorsHeader = add(new MenuComponentLabel(0, 0, 0, 0, "Generators", true));
    private final MenuComponentLabel settingsHeader = add(new MenuComponentLabel(0, 0, 0, 0, "Settings", true));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Stop generating reactors and return to the editor screen"));
    private final MenuComponentLabel threadsLabel = add(new MenuComponentLabel(0, 0, 0, 64, "No Threads", true));
    private final MenuComponentMinimalistButton addThread = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Add Thread", true, true).setTooltip("Add another thread to the generator\nMore threads generally means faster generation, but may slow down your computer\nHaving excessive amounts of threads can also slow down generation"));
    private final MenuComponentMinimalistButton removeThread = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Remove Thread", true, true).setTooltip("Remove a thread from the generator\nMore threads generally means faster generation, but may slow down your computer\nHaving excessive amounts of threads can also slow down generation"));
    private final MenuComponentMinimaList multiblockLists = add(new MenuComponentMinimaList(0, 0, 0, 0, 64));
    private Tab tab = Tab.SETTINGS;
    private final ArrayList<MultiblockGenerator> multiblockGenerators;
    private MultiblockGenerator generator = null;
    private int lastIndex;
    private int threads = 0;
    private int crashedThreadTicks = 200;//ns
    private int crashedThreadThreshold = 10;
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
            ArrayList<Range<Block>> allowedBlocks = new ArrayList<>();
            for(simplelibrary.opengl.gui.components.MenuComponent c : blocks.components){
                MenuComponentToggleBlock t = (MenuComponentToggleBlock)c;
                if(t.enabled)allowedBlocks.add(new Range<>(t.block,t.min,t.max==0?Integer.MAX_VALUE:t.max));
            }
            generator.refreshSettingsFromGUI(allowedBlocks);
            if(threads<=0){
                try{
                    generator.importMultiblock(multiblock);
                }catch(MissingConfigurationEntryException ex){
                    throw new RuntimeException(ex);
                }
                threads = 1;
            }
            threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
        });
        done.addActionListener((e) -> {
            if(generator!=null)generator.stopAllThreads();
            threads = 0;
            Multiblock generated = generator.getMainMultiblock();
            if(generated!=null)editor.multiblock.action(new GenerateAction(generated), true, true);
            gui.open(new MenuTransition(gui, this, editor, MenuTransition.SlideTransition.slideTo(0, 1), 5));
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
            if(threads>0&&generator.getCrashedThreads(crashedThreadTicks*50_000_000L)>crashedThreadThreshold){
                threads = 0;
                threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
                Sys.error(ErrorLevel.severe, "Stopping generation...", new RuntimeException(crashedThreadThreshold+" generation threads crashed in "+crashedThreadTicks+" ticks!"), ErrorCategory.threading);
            }
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
                onGUIOpened();
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent m : components){
            m.x = m.y = m.width = m.height = -1;
        }
        settings.width = output.width = gui.helper.displayWidth()/3;
        settings.x = gui.helper.displayWidth()-settings.width-output.width;
        output.x = settings.x+settings.width;
        output.y = settings.y = 0;
        output.height = settings.height = 64;
        switch(tab){
            case SETTINGS:
                done.height = 64;
                done.x = 0;
                done.y = 0;
                done.width = gui.helper.displayWidth()/4;
                blocksHeader.x = 0;
                blocksHeader.y = settings.height;
                blocksHeader.width = blocks.width = gui.helper.displayWidth()/4;
                blocksHeader.height = settings.height;
                blocks.x = 0;
                blocks.y = settings.height*2;
                blocks.height = gui.helper.displayHeight()-blocks.y;
                generatorsHeader.x = gui.helper.displayWidth()/4;
                generatorsHeader.y = settings.height;
                generatorsHeader.width = generators.width = gui.helper.displayWidth()/4;
                generatorsHeader.height = settings.height;
                generators.x = gui.helper.displayWidth()/4;
                generators.y = settings.height*2;
                generators.width = gui.helper.displayWidth()/4;
                generators.height = gui.helper.displayHeight()-generators.y;
                for(simplelibrary.opengl.gui.components.MenuComponent c : generators.components){
                    c.width = generators.width-(generators.hasVertScrollbar()?generators.vertScrollbarWidth:0);
                }
                settingsHeader.x = gui.helper.displayWidth()/2;
                settingsHeader.y = settings.height;
                settingsHeader.width = gui.helper.displayWidth()/2;
                settingsHeader.height = settings.height;
                generatorSettings.x = gui.helper.displayWidth()/2;
                generatorSettings.width = gui.helper.displayWidth()/4;
                generatorSettings.y = settings.height*2;
                generatorSettings.width = gui.helper.displayWidth()/4;
                generatorSettings.height = gui.helper.displayHeight()-generatorSettings.y;
                for(simplelibrary.opengl.gui.components.MenuComponent c : generatorSettings.components){
                    c.width = generatorSettings.width-(generatorSettings.hasVertScrollbar()?generatorSettings.vertScrollbarWidth:0);
                }
                multiblockSettings.x = gui.helper.displayWidth()*3/4;
                multiblockSettings.y = settings.height*2;
                multiblockSettings.width = gui.helper.displayWidth()/4;
                multiblockSettings.height = gui.helper.displayHeight()-multiblockSettings.y;
                for(simplelibrary.opengl.gui.components.MenuComponent c : multiblockSettings.components){
                    c.width = multiblockSettings.width-(multiblockSettings.hasVertScrollbar()?multiblockSettings.vertScrollbarWidth:0);
                }
                break;
            case GENERATE:
                threadsLabel.x = 0;
                threadsLabel.y = 64;
                threadsLabel.width = gui.helper.displayWidth()/3;
                threadsLabel.height = 48;
                addThread.x = threadsLabel.width;
                addThread.y = 64;
                addThread.width = gui.helper.displayWidth()/3;
                addThread.height = 48;
                removeThread.x = addThread.x+addThread.width;
                removeThread.y = 64;
                removeThread.width = gui.helper.displayWidth()-removeThread.x;
                removeThread.height = 48;
                multiblockLists.x = 0;
                multiblockLists.y = threadsLabel.height+settings.height;
                multiblockLists.width = gui.helper.displayWidth();
                multiblockLists.height = gui.helper.displayHeight()-multiblockLists.y;
                for(simplelibrary.opengl.gui.components.MenuComponent m : multiblockLists.components){
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