package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MultiblockConfigurationMenu;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuAddonBlockConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuBlockConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuComponentAddonBlock;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuComponentBlock;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr.MenuComponentPossibleAddonBlock;
import net.ncplanner.plannerator.planner.gui.menu.configuration.tree.MenuPlacementRuleTree;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuOverhaulMSRConfiguration extends MultiblockConfigurationMenu{
    public MenuOverhaulMSRConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul MSR");
        addSettingInt("Minimum Size", configuration.overhaul.fissionMSR::getMinSize, configuration.overhaul.fissionMSR::setMinSize).setTooltip("The minimum size of this multiblock");
        addSettingFloat("Sparsity Penalty Mult", configuration.overhaul.fissionMSR::getSparsityPenaltyMult, configuration.overhaul.fissionMSR::setSparsityPenaltyMult);
        addSettingInt("Neutron Reach", configuration.overhaul.fissionMSR::getNeutronReach, configuration.overhaul.fissionMSR::setNeutronReach).setTooltip("The maximum length of moderator lines");
        addSettingRow();
        addSettingInt("Maximum Size", configuration.overhaul.fissionMSR::getMaxSize, configuration.overhaul.fissionMSR::setMaxSize).setTooltip("The maximum size of this multiblock");
        addSettingFloat("Sparsity Penalty Threshold", configuration.overhaul.fissionMSR::getSparsityPenaltyThreshold, configuration.overhaul.fissionMSR::setSparsityPenaltyThreshold);
        addSettingInt("Cooling Efficiency Leniency", configuration.overhaul.fissionMSR::getCoolingEfficiencyLeniency, configuration.overhaul.fissionMSR::setCoolingEfficiencyLeniency).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
        addList(() -> {return "Blocks ("+configuration.overhaul.fissionMSR.blocks.size()+")";}, "Add Block", () -> {
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.fissionMSR.blocks.add(b);
            Core.configuration.overhaul.fissionMSR.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        }, (list) -> {
            if(configuration.addon){
                FOR:for(Block b : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(b.recipes.isEmpty())continue;//no recipes
                    if(configuration.overhaul.fissionMSR.blocks.contains(b))continue;//that's a block from this addon
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block bl : configuration.overhaul.fissionMSR.allBlocks){
                        if(bl.name.equals(b.name)){
                            list.add(new MenuComponentAddonBlock(b, bl, () -> {//edit
                                gui.open(new MenuAddonBlockConfiguration(gui, this, configuration, b, bl));
                            }, () -> {//delete
                                configuration.overhaul.fissionMSR.allBlocks.remove(bl);
                                refresh();
                            }));
                            continue FOR;
                        }
                    }
                    list.add(new MenuComponentPossibleAddonBlock(b, () -> {//add
                        Block bl = new Block(b.name);
                        bl.fuelVessel = b.fuelVessel;
                        bl.moderator = b.moderator;
                        bl.reflector = b.reflector;
                        bl.irradiator = b.irradiator;
                        bl.heater = b.heater;
                        bl.shield = b.shield;
                        configuration.overhaul.fissionMSR.allBlocks.add(bl);
                        refresh();
                    }));
                }
            }
            for(Block b : configuration.overhaul.fissionMSR.blocks){
                if(b.parent!=null)continue;//that's a port; that gets edited in its parent's menu
                list.add(new MenuComponentBlock(b, () -> {//edit
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
                }, () -> {//delete
                    configuration.overhaul.fissionMSR.blocks.remove(b);
                    Core.configuration.overhaul.fissionMSR.allBlocks.remove(b);
                    refresh();
                }));
            }
        }, "View Placement Rule Tree", ()->{
            gui.open(new MenuPlacementRuleTree(gui, this, configuration.overhaul.fissionMSR.allBlocks));
        }, !configuration.addon);
    }
}