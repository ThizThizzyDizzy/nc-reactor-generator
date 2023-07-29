package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.Range;
import net.ncplanner.plannerator.multiblock.editor.action.GenerateAction;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.multiblock.generator.setting.Setting;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.generator.MenuComponentMultiblockDisplay;
import net.ncplanner.plannerator.planner.gui.menu.component.generator.MenuComponentMultiblockGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.generator.MenuComponentToggleBlock;
public class MenuOldGenerator extends Menu{
    private final Multiblock<AbstractBlock> multiblock;
    private final Button settings = add(new Button(0, 0, 0, 64, "Settings", false).setTooltip("Modify generator settings\nThis does not stop the generator\nSettings are not applied until you click Generate"));
    private final Button output = add(new Button(0, 0, 0, 64, "Generate", true).setTooltip("Starts generating reactors or applies new settings"));
    private final MulticolumnList blocks = add(new MulticolumnList(0, 0, 0, 0, 64, 64, 32));
    private final SingleColumnList generators = add(new SingleColumnList(0, 0, 0, 0, 32));
    private final SingleColumnList generatorSettings = add(new SingleColumnList(0, 0, 0, 0, 32));
    private final SingleColumnList multiblockSettings = add(new SingleColumnList(0, 0, 0, 0, 32));
    private final Label blocksHeader = add(new Label(0, 0, 0, 0, "Blocks", true));
    private final Label generatorsHeader = add(new Label(0, 0, 0, 0, "Generators", true));
    private final Label settingsHeader = add(new Label(0, 0, 0, 0, "Settings", true));
    private final Button done = add(new Button("Done", true).setTooltip("Stop generating reactors and return to the editor screen"));
    private final Label threadsLabel = add(new Label(0, 0, 0, 64, "No Threads", true));
    private final Button addThread = add(new Button(0, 0, 0, 64, "Add Thread", true).setTooltip("Add another thread to the generator\nMore threads generally means faster generation, but may slow down your computer\nHaving excessive amounts of threads can also slow down generation"));
    private final Button removeThread = add(new Button(0, 0, 0, 64, "Remove Thread", true).setTooltip("Remove a thread from the generator\nMore threads generally means faster generation, but may slow down your computer\nHaving excessive amounts of threads can also slow down generation"));
    private final SingleColumnList multiblockLists = add(new SingleColumnList(0, 0, 0, 0, 64));
    private Tab tab = Tab.SETTINGS;
    private final ArrayList<MultiblockGenerator> multiblockGenerators;
    private MultiblockGenerator generator = null;
    private int lastIndex;
    private int threads = 0;
    private int crashedThreadTicks = 200;//ns
    private int crashedThreadThreshold = 10;
    public MenuOldGenerator(GUI gui, MenuEdit editor, Multiblock<AbstractBlock> multiblock){
        super(gui, editor);
        this.multiblock = multiblock;
        settings.addAction(() -> {
            settings.enabled = false;
            output.enabled = true;
            tab = Tab.SETTINGS;
        });
        output.addAction(() -> {
            settings.enabled = true;
            output.enabled = false;
            tab = Tab.GENERATE;
            ArrayList<Range<AbstractBlock>> allowedBlocks = new ArrayList<>();
            for(Component c : blocks.components){
                MenuComponentToggleBlock t = (MenuComponentToggleBlock)c;
                if(t.enabled)allowedBlocks.add(new Range<>(t.block,t.min,t.max==0?Integer.MAX_VALUE:t.max));
            }
            generator.setAllowedBlocks(allowedBlocks);
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
        done.addAction(() -> {
            if(generator!=null)generator.stopAllThreads();
            threads = 0;
            Multiblock generated = generator.getMainMultiblock();
            if(generated!=null)editor.multiblock.action(new GenerateAction(generated), true, true);
            gui.open(new MenuTransition(gui, this, editor, MenuTransition.SlideTransition.slideTo(0, 1), 5));
        });
        addThread.addAction(() -> {
            threads++;
            threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
        });
        removeThread.addAction(() -> {
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
    public void render2d(double deltaTime){
        if(generator!=null){
            if(threads>0&&generator.getCrashedThreads(crashedThreadTicks*50_000_000L)>crashedThreadThreshold){
                threads = 0;
                threadsLabel.text = threads+" Thread"+(threads==1?"":"s");
                Core.error("Stopping generation...", new RuntimeException(crashedThreadThreshold+" generation threads crashed in "+crashedThreadTicks+" ticks!"));
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
                    MulticolumnList lst = multiblockLists.add(new MulticolumnList(0, 0, 0, 0, 300, 800, 48));
                    for(Multiblock mb : mbs){
                        lst.add(new MenuComponentMultiblockDisplay(mb));
                    }
                }
            }else{
                for(int i = 0; i<multiblocks.length; i++){
                    ArrayList<Multiblock> mbs = multiblocks[i];
                    MulticolumnList lst = (MulticolumnList)multiblockLists.components.get(i);
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
        super.render2d(deltaTime);
    }
    @Override
    public void onOpened(){
        if(multiblockGenerators.isEmpty()){
            gui.open(parent);
            Core.error("No generators available for "+multiblock.getDefinitionName()+"!", null);
        }
        blocks.components.clear();
        for(AbstractBlock b : multiblock.getAvailableBlocks()){
            blocks.add(new MenuComponentToggleBlock(b));
        }
        generatorSettings.components.clear();
        for(Setting setting : generator.settings){
            setting.buildComponents(generatorSettings);
        }
        multiblockSettings.components.clear();
        multiblock.addGeneratorSettings(multiblockSettings);
    }
    @Override
    public void drawBackground(double deltaTime){
        if(generators.getSelectedIndex()!=lastIndex){
            int idx = generators.getSelectedIndex();
            if(idx!=-1){
                generator = multiblockGenerators.get(generators.getSelectedIndex()).newInstance(multiblock);
                lastIndex = idx;
                onOpened();
            }
        }
        for(Component m : components){
            m.x = m.y = m.width = m.height = -1;
        }
        settings.width = output.width = gui.getWidth()/3;
        settings.x = gui.getWidth()-settings.width-output.width;
        output.x = settings.x+settings.width;
        output.y = settings.y = 0;
        output.height = settings.height = 64;
        switch(tab){
            case SETTINGS:
                done.height = 64;
                done.x = 0;
                done.y = 0;
                done.width = gui.getWidth()/4;
                blocksHeader.x = 0;
                blocksHeader.y = settings.height;
                blocksHeader.width = blocks.width = gui.getWidth()/4;
                blocksHeader.height = settings.height;
                blocks.x = 0;
                blocks.y = settings.height*2;
                blocks.height = gui.getHeight()-blocks.y;
                generatorsHeader.x = gui.getWidth()/4;
                generatorsHeader.y = settings.height;
                generatorsHeader.width = generators.width = gui.getWidth()/4;
                generatorsHeader.height = settings.height;
                generators.x = gui.getWidth()/4;
                generators.y = settings.height*2;
                generators.width = gui.getWidth()/4;
                generators.height = gui.getHeight()-generators.y;
                for(Component c : generators.components){
                    c.width = generators.width-(generators.hasVertScrollbar()?generators.vertScrollbarWidth:0);
                }
                settingsHeader.x = gui.getWidth()/2;
                settingsHeader.y = settings.height;
                settingsHeader.width = gui.getWidth()/2;
                settingsHeader.height = settings.height;
                generatorSettings.x = gui.getWidth()/2;
                generatorSettings.width = gui.getWidth()/4;
                generatorSettings.y = settings.height*2;
                generatorSettings.width = gui.getWidth()/4;
                generatorSettings.height = gui.getHeight()-generatorSettings.y;
                for(Component c : generatorSettings.components){
                    c.width = generatorSettings.width-(generatorSettings.hasVertScrollbar()?generatorSettings.vertScrollbarWidth:0);
                }
                multiblockSettings.x = gui.getWidth()*3/4;
                multiblockSettings.y = settings.height*2;
                multiblockSettings.width = gui.getWidth()/4;
                multiblockSettings.height = gui.getHeight()-multiblockSettings.y;
                for(Component c : multiblockSettings.components){
                    c.width = multiblockSettings.width-(multiblockSettings.hasVertScrollbar()?multiblockSettings.vertScrollbarWidth:0);
                }
                break;
            case GENERATE:
                threadsLabel.x = 0;
                threadsLabel.y = 64;
                threadsLabel.width = gui.getWidth()/3;
                threadsLabel.height = 48;
                addThread.x = threadsLabel.width;
                addThread.y = 64;
                addThread.width = gui.getWidth()/3;
                addThread.height = 48;
                removeThread.x = addThread.x+addThread.width;
                removeThread.y = 64;
                removeThread.width = gui.getWidth()-removeThread.x;
                removeThread.height = 48;
                multiblockLists.x = 0;
                multiblockLists.y = threadsLabel.height+settings.height;
                multiblockLists.width = gui.getWidth();
                multiblockLists.height = gui.getHeight()-multiblockLists.y;
                for(Component m : multiblockLists.components){
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