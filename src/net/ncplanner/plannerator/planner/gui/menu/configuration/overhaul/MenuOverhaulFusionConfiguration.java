package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MultiblockConfigurationMenu;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuAddonBlockConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuBlockConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuComponentAddonBlock;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuComponentBlock;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuComponentCoolantRecipe;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuComponentPossibleAddonBlock;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuComponentRecipe;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuCoolantRecipeConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion.MenuRecipeConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.tree.MenuPlacementRuleTree;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuOverhaulFusionConfiguration extends MultiblockConfigurationMenu{
    public MenuOverhaulFusionConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul Fusion");
        addSettingInt("Minimum Inner Radius", configuration.overhaul.fusion::getMinInnerRadius, configuration.overhaul.fusion::setMinInnerRadius);
        addSettingInt("Minimum Core Size", configuration.overhaul.fusion::getMinCoreSize, configuration.overhaul.fusion::setMinCoreSize);
        addSettingInt("Minimum Toroid Width", configuration.overhaul.fusion::getMinToroidWidth, configuration.overhaul.fusion::setMinToroidWidth);
        addSettingInt("Minimum Lining Thickness", configuration.overhaul.fusion::getMinLiningThickness, configuration.overhaul.fusion::setMinLiningThickness);
        addSettingRow();
        addSettingInt("Maximum Inner Radius", configuration.overhaul.fusion::getMaxInnerRadius, configuration.overhaul.fusion::setMaxInnerRadius);
        addSettingInt("Maximum Core Size", configuration.overhaul.fusion::getMaxCoreSize, configuration.overhaul.fusion::setMaxCoreSize);
        addSettingInt("Maximum Toroid Width", configuration.overhaul.fusion::getMaxToroidWidth, configuration.overhaul.fusion::setMaxToroidWidth);
        addSettingInt("Maximum Lining Thickness", configuration.overhaul.fusion::getMaxLiningThickness, configuration.overhaul.fusion::setMaxLiningThickness);
        addSettingRow();
        addSettingFloat("Sparsity Penalty Mult", configuration.overhaul.fusion::getSparsityPenaltyMult, configuration.overhaul.fusion::setSparsityPenaltyMult);
        addSettingFloat("Sparsity Penalty Threshold", configuration.overhaul.fusion::getSparsityPenaltyThreshold, configuration.overhaul.fusion::setSparsityPenaltyThreshold);
        addSettingInt("Cooling Efficiency Leniency", configuration.overhaul.fusion::getCoolingEfficiencyLeniency, configuration.overhaul.fusion::setCoolingEfficiencyLeniency).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
        addList(() -> {return "Blocks ("+configuration.overhaul.fusion.blocks.size()+")";}, "Add Block", () -> {
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.fusion.blocks.add(b);
            Core.configuration.overhaul.fusion.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        }, (list) -> {
            if(configuration.addon){
                FOR:for(Block b : Core.configuration.overhaul.fusion.allBlocks){
                    if(b.recipes.isEmpty())continue;//no recipes
                    if(configuration.overhaul.fusion.blocks.contains(b))continue;//that's a block from this addon
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block bl : configuration.overhaul.fusion.allBlocks){
                        if(bl.name.equals(b.name)){
                            list.add(new MenuComponentAddonBlock(b, bl, () -> {//edit
                                gui.open(new MenuAddonBlockConfiguration(gui, this, configuration, b, bl));
                            }, () -> {//delete
                                configuration.overhaul.fusion.allBlocks.remove(bl);
                                refresh();
                            }));
                            continue FOR;
                        }
                    }
                    list.add(new MenuComponentPossibleAddonBlock(b, () -> {//add
                        Block bl = new Block(b.name);
                        bl.breedingBlanket = b.breedingBlanket;
                        bl.shielding = b.shielding;
                        bl.reflector = b.reflector;
                        bl.heatsink = b.heatsink;
                        configuration.overhaul.fusion.allBlocks.add(bl);
                        refresh();
                    }));
                }
            }
            for(Block b : configuration.overhaul.fusion.blocks){
                list.add(new MenuComponentBlock(b, () -> {//edit
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
                }, () -> {//delete
                    configuration.overhaul.fusion.blocks.remove(b);
                    Core.configuration.overhaul.fusion.allBlocks.remove(b);
                    refresh();
                }));
            }
        }, "View Placement Rule Tree", ()->{
            gui.open(new MenuPlacementRuleTree(gui, this, configuration.overhaul.fusion.allBlocks));
        }, !configuration.addon);
        addList(()->{return "Coolant Recipes ("+configuration.overhaul.fusion.coolantRecipes.size()+")";}, "Add Coolant Recipe", () -> {
            CoolantRecipe r = new CoolantRecipe("input_fluid", "output_fluid", 0, 0);
            configuration.overhaul.fusion.coolantRecipes.add(r);
            Core.configuration.overhaul.fusion.allCoolantRecipes.add(r);
            gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, r));
        }, (list) -> {
            for(CoolantRecipe r : configuration.overhaul.fusion.coolantRecipes){
                list.add(new MenuComponentCoolantRecipe(r, () -> {//edit
                    gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, r));
                }, () -> {//delete
                    configuration.overhaul.fusion.coolantRecipes.remove(r);
                    Core.configuration.overhaul.fusion.allCoolantRecipes.remove(r);
                    refresh();
                }));
            }
        });
        addList(()->{return "Recipes ("+configuration.overhaul.fusion.recipes.size()+")";}, "Add Recipe", () -> {
            Recipe r = new Recipe("input_fluid", "output_fluid", 0, 0, 0, 0);
            configuration.overhaul.fusion.recipes.add(r);
            Core.configuration.overhaul.fusion.allRecipes.add(r);
            gui.open(new MenuRecipeConfiguration(gui, this, configuration, r));
        }, (list) -> {
            for(Recipe r : configuration.overhaul.fusion.recipes){
                list.add(new MenuComponentRecipe(r, () -> {//edit
                    gui.open(new MenuRecipeConfiguration(gui, this, configuration, r));
                }, () -> {//delete
                    configuration.overhaul.fusion.recipes.remove(r);
                    Core.configuration.overhaul.fusion.allRecipes.remove(r);
                    refresh();
                }));
            }
        });
    }
}