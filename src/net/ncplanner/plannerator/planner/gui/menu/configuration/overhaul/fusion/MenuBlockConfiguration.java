package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.PlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MenuComponentPlacementRule;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MenuPlacementRuleConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.PartConfigurationMenu;
public class MenuBlockConfiguration extends PartConfigurationMenu{
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        addMainSection(null, block::getTexture, block::setTexture, "The ingame name of this block. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of this block.", ()->{return block.name;}, ()->{return block.displayName;}, ()->{return block.legacyNames;}, block::setName, block::setDisplayName, block::setLegacyNames);
        addSettingBoolean("Functional", block::isFunctional, block::setFunctional);
        addSettingBoolean("Core", block::isCore, block::setCore);
        addSettingBoolean("Connector", block::isConnector, block::setConnector);
        addSettingBoolean("Electromagnet", block::isElectromagnet, block::setElectromagnet);
        finishSettingRow();
        addSettingBoolean("Heating Blanket", block::isHeatingBlanket, block::setHeatingBlanket);
        addSettingBoolean("Conductor", block::isConductor, block::setConductor);
        addSettingBoolean("Can Cluster", block::isCluster, block::setCluster);
        addSettingBoolean("Creates Cluster", block::isCreateCluster, block::setCreateCluster);
        addColumnSectionToggle("Breeding Blanket", block::isBreedingBlanket, block::setBreedingBlanket);
        addColumnSubsectionToggle("Has base stats", block::isBreedingBlanketHasBaseStats, block::setBreedingBlanketHasBaseStats);
        addColumnSettingFloat("Efficiency", block::getBreedingBlanketEfficiency, block::setBreedingBlanketEfficiency);
        addColumnSettingFloat("Heat", block::getBreedingBlanketHeat, block::setBreedingBlanketHeat);
        addColumnSettingBoolean("Augmented", block::isBreedingBlanketAugmented, block::setBreedingBlanketAugmented);
        finishColumn();
        addColumnSectionToggle("Reflector", block::isReflector, block::setReflector);
        addColumnSubsectionToggle("Has base stats", block::isReflectorHasBaseStats, block::setReflectorHasBaseStats);
        addColumnSettingFloat("Efficiency", block::getReflectorEfficiency, block::setReflectorEfficiency);
        addColumnSectionToggle("Heat Sink", block::isHeatsink, block::setHeatsink);
        addColumnSubsectionToggle("Has base stats", block::isHeatsinkHasBaseStats, block::setHeatsinkHasBaseStats);
        addColumnSettingInt("Cooling", block::getHeatsinkCooling, block::setHeatsinkCooling);
        finishColumn();
        addColumnSectionToggle("Shielding", block::isShielding, block::setShielding);
        addColumnSubsectionToggle("Has base stats", block::isShieldingHasBaseStats, block::setShieldingHasBaseStats);
        addColumnSettingFloat("Shieldiness", block::getShieldingShieldiness, block::setShieldingShieldiness);
        addList(()->{return "Block Recipes ("+block.recipes.size()+")";}, "New Recipe", ()->{
            BlockRecipe recipe = new BlockRecipe("nuclearcraft:input", "nuclearcraft:output");
            block.allRecipes.add(recipe);
            block.recipes.add(recipe);
            gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, block, recipe));
        }, (list)->{
            for(BlockRecipe recipe : block.recipes){
                list.add(new MenuComponentBlockRecipe(block, recipe, ()->{//edit
                    gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, block, recipe));
                }, ()->{//delete
                    block.allRecipes.remove(recipe);
                    block.recipes.remove(recipe);
                    refresh();
                }));
            }
        });
        addList(()->{return "Placement Rules ("+block.rules.size()+")";}, "New Rule", ()->{
            PlacementRule rule;
            block.rules.add(rule = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(
                    gui, this, configuration, rule,Core.configuration.overhaul.fusion,
                    net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.PlacementRule.BlockType.values()
            ));
        }, (list)->{
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
                list.add(new MenuComponentPlacementRule(rule, ()->{//edit
                    gui.open(new MenuPlacementRuleConfiguration(gui, this, configuration, rule, Core.configuration.overhaul.fusion, PlacementRule.BlockType.values()));
                }, ()->{//delete
                    block.rules.remove(rule);
                    refresh();
                }));
            }
        });
    }
}