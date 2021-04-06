package planner.menu.configuration.overhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.Block;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.overhaul.fissionsfr.MenuAddonBlockConfiguration;
import planner.menu.configuration.overhaul.fissionsfr.MenuBlockConfiguration;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentAddonBlock;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentBlock;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentCoolantRecipe;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentPossibleAddonBlock;
import planner.menu.configuration.overhaul.fissionsfr.MenuCoolantRecipeConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuOverhaulSFRConfiguration extends ConfigurationMenu{
    private final MenuComponentMinimalistTextBox minSize, maxSize, neutronReach, sparsityPenaltyMult, sparsityPenaltyThreshold, coolingEfficiencyLeniency;
    private final MenuComponentLabel blocksLabel, coolantRecipesLabel;
    private final MenuComponentMinimaList blocksList, coolantRecipesList;
    private final MenuComponentMinimalistButton addBlock, addCoolantRecipe;
    private boolean refreshNeeded = false;
    public MenuOverhaulSFRConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul SFR");
        minSize = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Size").setIntFilter()).setTooltip("The minimum size of this multiblock");
        maxSize = add(new MenuComponentMinimalistTextBox(sidebar.width, minSize.height, 0, configuration.addon?0:48, "", true, "Maximum Size").setIntFilter()).setTooltip("The maximum size of this multiblock");
        sparsityPenaltyMult = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Sparsity Penalty Mult").setFloatFilter());
        sparsityPenaltyThreshold = add(new MenuComponentMinimalistTextBox(sidebar.width, sparsityPenaltyMult.height, 0, configuration.addon?0:48, "", true, "Sparsity Penalty Threshold").setFloatFilter());
        neutronReach = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Neutron Reach").setIntFilter()).setTooltip("The maximum length of moderator lines");
        coolingEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(sidebar.width, neutronReach.height, 0, configuration.addon?0:48, "", true, "Cooling Efficiency Leniency").setIntFilter()).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
        blocksLabel = add(new MenuComponentLabel(sidebar.width, maxSize.y+maxSize.height, 0, 48, "Blocks"));
        coolantRecipesLabel = add(new MenuComponentLabel(sidebar.width, maxSize.y+maxSize.height, 0, 48, "Coolant Recipes"));
        blocksList = add(new MenuComponentMinimaList(sidebar.width, blocksLabel.y+blocksLabel.height, 0, 0, 16));
        coolantRecipesList = add(new MenuComponentMinimaList(sidebar.width, coolantRecipesLabel.y+coolantRecipesLabel.height, 0, 0, 16));
        addBlock = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Block", true, true));
        addCoolantRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Coolant Recipe", true, true));
        addBlock.addActionListener((e) -> {
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.fissionSFR.blocks.add(b);
            Core.configuration.overhaul.fissionSFR.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        });
        addCoolantRecipe.addActionListener((e) -> {
            CoolantRecipe r = new CoolantRecipe("input_fluid", "output_fluid", 0, 0);
            configuration.overhaul.fissionSFR.coolantRecipes.add(r);
            Core.configuration.overhaul.fissionSFR.allCoolantRecipes.add(r);
            gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, r));
        });
    }
    @Override
    public void onGUIOpened(){
        if(configuration.overhaul.fissionSFR.blocks.size()>0)blocksLabel.text = "Blocks ("+configuration.overhaul.fissionSFR.blocks.size()+")";
        if(configuration.overhaul.fissionSFR.coolantRecipes.size()>0)coolantRecipesLabel.text = "Coolant Recipes ("+configuration.overhaul.fissionSFR.coolantRecipes.size()+")";
        minSize.text = configuration.overhaul.fissionSFR.minSize+"";
        maxSize.text = configuration.overhaul.fissionSFR.maxSize+"";
        neutronReach.text = configuration.overhaul.fissionSFR.neutronReach+"";
        sparsityPenaltyMult.text = configuration.overhaul.fissionSFR.sparsityPenaltyMult+"";
        sparsityPenaltyThreshold.text = configuration.overhaul.fissionSFR.sparsityPenaltyThreshold+"";
        coolingEfficiencyLeniency.text = configuration.overhaul.fissionSFR.coolingEfficiencyLeniency+"";
        blocksList.components.clear();
        if(configuration.addon){
            FOR:for(Block b : Core.configuration.overhaul.fissionSFR.allBlocks){
                if(b.recipes.isEmpty())continue;//no recipes
                if(configuration.overhaul.fissionSFR.blocks.contains(b))continue;//that's a block from this addon
                for(multiblock.configuration.overhaul.fissionsfr.Block bl : configuration.overhaul.fissionSFR.allBlocks){
                    if(bl.name.equals(b.name)){
                        blocksList.add(new MenuComponentAddonBlock(b, bl));
                        continue FOR;
                    }
                }
                blocksList.add(new MenuComponentPossibleAddonBlock(b));
            }
        }
        for(Block b : configuration.overhaul.fissionSFR.blocks){
            if(b.parent!=null)continue;//that's a port; that gets edited in its parent's menu
            blocksList.add(new MenuComponentBlock(b));
        }
        coolantRecipesList.components.clear();
        for(CoolantRecipe f : configuration.overhaul.fissionSFR.coolantRecipes){
            coolantRecipesList.add(new MenuComponentCoolantRecipe(f));
        }
    }
    @Override
    public void onGUIClosed(){
        configuration.overhaul.fissionSFR.minSize = Integer.parseInt(minSize.text);
        configuration.overhaul.fissionSFR.maxSize = Integer.parseInt(maxSize.text);
        configuration.overhaul.fissionSFR.neutronReach = Integer.parseInt(neutronReach.text);
        configuration.overhaul.fissionSFR.sparsityPenaltyMult = Float.parseFloat(sparsityPenaltyMult.text);
        configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = Float.parseFloat(sparsityPenaltyThreshold.text);
        configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = Integer.parseInt(coolingEfficiencyLeniency.text);
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
        addBlock.width = addCoolantRecipe.width = blocksLabel.width = coolantRecipesLabel.width = blocksList.width = coolantRecipesList.width = w/2;
        addCoolantRecipe.x = coolantRecipesLabel.x = coolantRecipesList.x = blocksLabel.x+blocksLabel.width;
        addBlock.y = addCoolantRecipe.y = Core.helper.displayHeight()-addBlock.height;
        blocksList.height = coolantRecipesList.height = addBlock.y-(coolantRecipesLabel.y+coolantRecipesLabel.height);
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : blocksList.components){
            if(c instanceof MenuComponentBlock){
                if(button==((MenuComponentBlock) c).delete){
                    configuration.overhaul.fissionSFR.blocks.remove(((MenuComponentBlock) c).block);
                    Core.configuration.overhaul.fissionSFR.allBlocks.remove(((MenuComponentBlock) c).block);
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
                    b.fuelCell = ((MenuComponentPossibleAddonBlock)c).block.fuelCell;
                    b.moderator = ((MenuComponentPossibleAddonBlock)c).block.moderator;
                    b.reflector = ((MenuComponentPossibleAddonBlock)c).block.reflector;
                    b.irradiator = ((MenuComponentPossibleAddonBlock)c).block.irradiator;
                    b.heatsink = ((MenuComponentPossibleAddonBlock)c).block.heatsink;
                    b.shield = ((MenuComponentPossibleAddonBlock)c).block.shield;
                    configuration.overhaul.fissionSFR.allBlocks.add(b);
                    refreshNeeded = true;
                    return;
                }
            }
            if(c instanceof MenuComponentAddonBlock){
                if(button==((MenuComponentAddonBlock) c).delete){
                    configuration.overhaul.fissionSFR.allBlocks.remove(((MenuComponentAddonBlock)c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentAddonBlock)c).edit){
                    gui.open(new MenuAddonBlockConfiguration(gui, this, configuration, ((MenuComponentAddonBlock)c).parent, ((MenuComponentAddonBlock) c).block));
                    return;
                }
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : coolantRecipesList.components){
            if(c instanceof MenuComponentCoolantRecipe){
                if(button==((MenuComponentCoolantRecipe) c).delete){
                    configuration.overhaul.fissionSFR.coolantRecipes.remove(((MenuComponentCoolantRecipe) c).coolantRecipe);
                    Core.configuration.overhaul.fissionSFR.allCoolantRecipes.remove(((MenuComponentCoolantRecipe) c).coolantRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentCoolantRecipe) c).edit){
                    gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, ((MenuComponentCoolantRecipe) c).coolantRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}