package planner.menu.configuration.overhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.Block;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.Core;
import planner.menu.configuration.tree.MenuPlacementRuleTree;
import planner.menu.configuration.MultiblockConfigurationMenu;
import planner.menu.configuration.overhaul.fissionsfr.MenuAddonBlockConfiguration;
import planner.menu.configuration.overhaul.fissionsfr.MenuBlockConfiguration;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentAddonBlock;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentBlock;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentCoolantRecipe;
import planner.menu.configuration.overhaul.fissionsfr.MenuComponentPossibleAddonBlock;
import planner.menu.configuration.overhaul.fissionsfr.MenuCoolantRecipeConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuOverhaulSFRConfiguration extends MultiblockConfigurationMenu{
    public MenuOverhaulSFRConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul SFR");
        addSettingInt("Minimum Size", configuration.overhaul.fissionSFR::getMinSize, configuration.overhaul.fissionSFR::setMinSize).setTooltip("The minimum size of this multiblock");
        addSettingFloat("Sparsity Penalty Mult", configuration.overhaul.fissionSFR::getSparsityPenaltyMult, configuration.overhaul.fissionSFR::setSparsityPenaltyMult);
        addSettingInt("Neutron Reach", configuration.overhaul.fissionSFR::getNeutronReach, configuration.overhaul.fissionSFR::setNeutronReach).setTooltip("The maximum length of moderator lines");
        addSettingRow();
        addSettingInt("Maximum Size", configuration.overhaul.fissionSFR::getMaxSize, configuration.overhaul.fissionSFR::setMaxSize).setTooltip("The maximum size of this multiblock");
        addSettingFloat("Sparsity Penalty Threshold", configuration.overhaul.fissionSFR::getSparsityPenaltyThreshold, configuration.overhaul.fissionSFR::setSparsityPenaltyThreshold);
        addSettingInt("Cooling Efficiency Leniency", configuration.overhaul.fissionSFR::getCoolingEfficiencyLeniency, configuration.overhaul.fissionSFR::setCoolingEfficiencyLeniency).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
        addList(()->{return "Blocks ("+configuration.overhaul.fissionSFR.blocks.size()+")";}, "Add Block", ()->{
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.fissionSFR.blocks.add(b);
            Core.configuration.overhaul.fissionSFR.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        }, (list)->{
            if(configuration.addon){
                FOR:for(Block b : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(b.recipes.isEmpty())continue;//no recipes
                    if(configuration.overhaul.fissionSFR.blocks.contains(b))continue;//that's a block from this addon
                    for(multiblock.configuration.overhaul.fissionsfr.Block bl : configuration.overhaul.fissionSFR.allBlocks){
                        if(bl.name.equals(b.name)){
                            list.add(new MenuComponentAddonBlock(b, bl, () -> {//edit
                                gui.open(new MenuAddonBlockConfiguration(gui, this, configuration, b, bl));
                            }, () -> {//delete
                                configuration.overhaul.fissionSFR.allBlocks.remove(bl);
                                refresh();
                            }));
                            continue FOR;
                        }
                    }
                    list.add(new MenuComponentPossibleAddonBlock(b, () -> {//add
                        Block bl = new Block(b.name);
                        bl.fuelCell = b.fuelCell;
                        bl.moderator = b.moderator;
                        bl.reflector = b.reflector;
                        bl.irradiator = b.irradiator;
                        bl.heatsink = b.heatsink;
                        bl.shield = b.shield;
                        configuration.overhaul.fissionSFR.allBlocks.add(bl);
                        refresh();
                    }));
                }
            }
            for(Block b : configuration.overhaul.fissionSFR.blocks){
                if(b.parent!=null)continue;//that's a port; that gets edited in its parent's menu
                list.add(new MenuComponentBlock(b, () -> {//edit
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
                }, () -> {//delete
                    configuration.overhaul.fissionSFR.blocks.remove(b);
                    Core.configuration.overhaul.fissionSFR.allBlocks.remove(b);
                    refresh();
                }));
            }
        }, "View Placement Rule Tree", ()->{
            gui.open(new MenuPlacementRuleTree(gui, this, configuration.overhaul.fissionSFR.allBlocks));
        }, !configuration.addon);
        addList(()->{return "Coolant Recipes ("+configuration.overhaul.fissionSFR.coolantRecipes.size()+")";}, "Add Coolant Recipe", ()->{
            CoolantRecipe r = new CoolantRecipe("input_fluid", "output_fluid", 0, 0);
            configuration.overhaul.fissionSFR.coolantRecipes.add(r);
            Core.configuration.overhaul.fissionSFR.allCoolantRecipes.add(r);
            gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, r));
        }, (list)->{
            for(CoolantRecipe r : configuration.overhaul.fissionSFR.coolantRecipes){
                list.add(new MenuComponentCoolantRecipe(r, () -> {//edit
                    gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, r));
                }, () -> {//delete
                    configuration.overhaul.fissionSFR.coolantRecipes.remove(r);
                    Core.configuration.overhaul.fissionSFR.allCoolantRecipes.remove(r);
                    refresh();
                }));
            }
        });
    }
}