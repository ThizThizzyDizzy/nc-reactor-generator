package planner.menu.configuration.overhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionmsr.Block;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.overhaul.fissionmsr.MenuAddonBlockConfiguration;
import planner.menu.configuration.overhaul.fissionmsr.MenuBlockConfiguration;
import planner.menu.configuration.overhaul.fissionmsr.MenuComponentAddonBlock;
import planner.menu.configuration.overhaul.fissionmsr.MenuComponentBlock;
import planner.menu.configuration.overhaul.fissionmsr.MenuComponentPossibleAddonBlock;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuOverhaulMSRConfiguration extends ConfigurationMenu{
    private final MenuComponentMinimalistTextBox minSize, maxSize, neutronReach, sparsityPenaltyMult, sparsityPenaltyThreshold, coolingEfficiencyLeniency;
    private final MenuComponentLabel blocksLabel;
    private final MenuComponentMinimaList blocksList;
    private final MenuComponentMinimalistButton addBlock;
    private boolean refreshNeeded = false;
    public MenuOverhaulMSRConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul MSR");
        minSize = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Size").setIntFilter()).setTooltip("The minimum size of this multiblock");
        maxSize = add(new MenuComponentMinimalistTextBox(sidebar.width, minSize.height, 0, configuration.addon?0:48, "", true, "Maximum Size").setIntFilter()).setTooltip("The maximum size of this multiblock");
        sparsityPenaltyMult = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Sparsity Penalty Mult").setFloatFilter());
        sparsityPenaltyThreshold = add(new MenuComponentMinimalistTextBox(sidebar.width, sparsityPenaltyMult.height, 0, configuration.addon?0:48, "", true, "Sparsity Penalty Threshold").setFloatFilter());
        neutronReach = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Neutron Reach").setIntFilter()).setTooltip("The maximum length of moderator lines");
        coolingEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(sidebar.width, neutronReach.height, 0, configuration.addon?0:48, "", true, "Cooling Efficiency Leniency").setIntFilter()).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
        blocksLabel = add(new MenuComponentLabel(sidebar.width, maxSize.y+maxSize.height, 0, 48, "Blocks"));
        blocksList = add(new MenuComponentMinimaList(sidebar.width, blocksLabel.y+blocksLabel.height, 0, 0, 16));
        addBlock = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Block", true, true));
        addBlock.addActionListener((e) -> {
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.fissionMSR.blocks.add(b);
            Core.configuration.overhaul.fissionMSR.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        });
    }
    @Override
    public void onGUIOpened(){
        if(configuration.overhaul.fissionMSR.blocks.size()>0)blocksLabel.text = "Blocks ("+configuration.overhaul.fissionMSR.blocks.size()+")";
        minSize.text = configuration.overhaul.fissionMSR.minSize+"";
        maxSize.text = configuration.overhaul.fissionMSR.maxSize+"";
        neutronReach.text = configuration.overhaul.fissionMSR.neutronReach+"";
        sparsityPenaltyMult.text = configuration.overhaul.fissionMSR.sparsityPenaltyMult+"";
        sparsityPenaltyThreshold.text = configuration.overhaul.fissionMSR.sparsityPenaltyThreshold+"";
        coolingEfficiencyLeniency.text = configuration.overhaul.fissionMSR.coolingEfficiencyLeniency+"";
        blocksList.components.clear();
        if(configuration.addon){
            FOR:for(Block b : Core.configuration.overhaul.fissionMSR.allBlocks){
                if(b.recipes.isEmpty())continue;//no recipes
                if(configuration.overhaul.fissionMSR.blocks.contains(b))continue;//that's a block from this addon
                for(multiblock.configuration.overhaul.fissionmsr.Block bl : configuration.overhaul.fissionMSR.allBlocks){
                    if(bl.name.equals(b.name)){
                        blocksList.add(new MenuComponentAddonBlock(b, bl));
                        continue FOR;
                    }
                }
                blocksList.add(new MenuComponentPossibleAddonBlock(b));
            }
        }
        for(Block b : configuration.overhaul.fissionMSR.blocks){
            if(b.parent!=null)continue;//that's a port; that gets edited in its parent's menu
            blocksList.add(new MenuComponentBlock(b));
        }
    }
    @Override
    public void onGUIClosed(){
        configuration.overhaul.fissionMSR.minSize = Integer.parseInt(minSize.text);
        configuration.overhaul.fissionMSR.maxSize = Integer.parseInt(maxSize.text);
        configuration.overhaul.fissionMSR.neutronReach = Integer.parseInt(neutronReach.text);
        configuration.overhaul.fissionMSR.sparsityPenaltyMult = Float.parseFloat(sparsityPenaltyMult.text);
        configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = Float.parseFloat(sparsityPenaltyThreshold.text);
        configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = Integer.parseInt(coolingEfficiencyLeniency.text);
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
        double w = gui.helper.displayWidth()-sidebar.width;
        minSize.width = maxSize.width = neutronReach.width = sparsityPenaltyMult.width = sparsityPenaltyThreshold.width = coolingEfficiencyLeniency.width = w/3;
        maxSize.y = sparsityPenaltyThreshold.y = coolingEfficiencyLeniency.y = minSize.y+minSize.height;;
        sparsityPenaltyMult.x = sparsityPenaltyThreshold.x = sidebar.width+w/3;
        neutronReach.x = coolingEfficiencyLeniency.x = sidebar.width+w*2/3;
        addBlock.width = blocksLabel.width = blocksList.width = w;
        addBlock.y = Core.helper.displayHeight()-addBlock.height;
        blocksList.height = addBlock.y-(blocksLabel.y+blocksLabel.height);
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : blocksList.components){
            if(c instanceof MenuComponentBlock){
                if(button==((MenuComponentBlock) c).delete){
                    configuration.overhaul.fissionMSR.blocks.remove(((MenuComponentBlock) c).block);
                    Core.configuration.overhaul.fissionMSR.allBlocks.remove(((MenuComponentBlock) c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlock) c).edit){
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, ((MenuComponentBlock) c).block));
                    return;
                }
            }
            if(c instanceof MenuComponentPossibleAddonBlock){
                if(button==((MenuComponentPossibleAddonBlock) c).add){
                    Block b = new Block(((MenuComponentPossibleAddonBlock)c).block.name);
                    b.fuelVessel = ((MenuComponentPossibleAddonBlock)c).block.fuelVessel;
                    b.moderator = ((MenuComponentPossibleAddonBlock)c).block.moderator;
                    b.reflector = ((MenuComponentPossibleAddonBlock)c).block.reflector;
                    b.irradiator = ((MenuComponentPossibleAddonBlock)c).block.irradiator;
                    b.heater = ((MenuComponentPossibleAddonBlock)c).block.heater;
                    b.shield = ((MenuComponentPossibleAddonBlock)c).block.shield;
                    configuration.overhaul.fissionMSR.allBlocks.add(b);
                    refreshNeeded = true;
                    return;
                }
            }
            if(c instanceof MenuComponentAddonBlock){
                if(button==((MenuComponentAddonBlock) c).delete){
                    configuration.overhaul.fissionMSR.allBlocks.remove(((MenuComponentAddonBlock)c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentAddonBlock)c).edit){
                    gui.open(new MenuAddonBlockConfiguration(gui, this, configuration, ((MenuComponentAddonBlock)c).parent, ((MenuComponentAddonBlock) c).block));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}