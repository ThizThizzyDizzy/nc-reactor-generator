package planner.menu.configuration.overhaul.fissionmsr;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import multiblock.configuration.overhaul.fissionmsr.PlacementRule;
import planner.Core;
import planner.menu.configuration.MenuComponentPlacementRule;
import planner.menu.configuration.MenuPlacementRuleConfiguration;
import planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockConfiguration extends PartConfigurationMenu{
    private final Block block;
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        addMainSection(null, block::getTexture, block::setTexture, "The ingame name of this block. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of this block.", ()->{return block.name;}, ()->{return block.displayName;}, ()->{return block.legacyNames;}, block::setName, block::setDisplayName, block::setLegacyNames);
        addPortSection(()->{return block.port==null?null:block.port.texture;}, (t)->{if(block.port!=null)block.port.setTexture(t);}, ()->{return block.port==null?null:block.port.portOutputTexture;}, (t)->{if(block.port!=null)block.port.setPortOutputTexture(t);}, "The ingame name of this block's access port. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of the port in input mode.", "The user-friendly name of the port in output mode.", ()->{return block.port==null?"":block.port.name;}, ()->{return block.port==null?null:block.port.displayName;}, ()->{return block.port==null?null:block.port.portOutputDisplayName;}, (s)->{if(block.port!=null)block.port.setName(s);}, (s)->{if(block.port!=null)block.port.setDisplayName(s);}, (s)->{if(block.port!=null)block.port.setPortOutputDisplayName(s);}, ()->{return block.port!=null;});
        addSettingBoolean("Functional", block::isFunctional, block::setFunctional).setTooltip("If set, this block will count against the sparsity penalty");
        addSettingBoolean("Casing", block::isCasing, block::setCasing).setTooltip("If set, this block can be placed in the multiblock casing walls");
        addSettingBoolean("Casing Edge", block::isCasingEdge, block::setCasingEdge).setTooltip("If set, this block can be placed in the multiblock casing edge");
        addSettingBoolean("Controller", block::isController, block::setController);
        finishSettingRow();
        addSettingBoolean("Blocks Line of Sight", block::isBlocksLOS, block::setBlocksLOS);
        addSettingBoolean("Conductor", block::isConductor, block::setConductor).setTooltip("If set, this block will connect clusters to the casing, but will not connect them together");
        addSettingBoolean("Can Cluster", block::isCluster, block::setCluster).setTooltip("If set, this block can be part of a cluster");
        addSettingBoolean("Creates Cluster", block::isCreateCluster, block::setCreateCluster).setTooltip("If set, this block will create a cluster");
        addColumnSectionToggle("Fuel Vessel", block::isFuelVessel, block::setFuelVessel);
        addColumnSubsectionToggle("Has base stats", block::isFuelVesselHasBaseStats, block::setFuelVesselHasBaseStats);
        addColumnSettingFloat("Efficiency", block::getFuelVesselEfficiency, block::setFuelVesselEfficiency);
        addColumnSettingInt("Heat", block::getFuelVesselHeat, block::setFuelVesselHeat);
        addColumnSettingInt("Criticality", block::getFuelVesselCriticality, block::setFuelVesselCriticality);
        addColumnSettingBoolean("Self-Priming", block::isFuelVesselSelfPriming, block::setFuelVesselSelfPriming);
        addColumnSectionToggle("Heater", block::isHeater, block::setHeater);
        addColumnSubsectionToggle("Has base stats", block::isHeaterHasBaseStats, block:: setHeaterHasBaseStats);
        addColumnSettingInt("Cooling", block::getHeaterCooling, block::setHeaterCooling);
        addColumnSectionToggle("Neutron Source", block::isSource, block::setSource);
        addColumnSettingFloat("Efficiency", block::getSourceEfficiency, block::setSourceEfficiency);
        finishColumn();
        addColumnSectionToggle("Reflector", block::isReflector, block::setReflector);
        addColumnSubsectionToggle("Has base stats", block::isReflectorHasBaseStats, block::setReflectorHasBaseStats);
        addColumnSettingFloat("Efficiency", block::getReflectorEfficiency, block::setReflectorEfficiency);
        addColumnSettingFloat("Reflectivity", block::getReflectorReflectivity, block::setReflectorReflectivity);
        addColumnSectionToggle("Moderator", block::isModerator, block::setModerator);
        addColumnSubsectionToggle("Has base stats", block::isModeratorHasBaseStats, block::setModeratorHasBaseStats);
        addColumnSettingFloat("Efficiency", block::getModeratorEfficiency, block::setModeratorEfficiency);
        addColumnSettingInt("Flux", block::getModeratorFlux, block::setModeratorFlux);
        addColumnSettingBoolean("Active", block::isModeratorActive, block::setModeratorActive);
        finishColumn();
        addColumnSectionToggle("Irradiator", block::isIrradiator, block::setIrradiator);
        addColumnSubsectionToggle("Has base stats", block::isIrradiatorHasBaseStats, block::setIrradiatorHasBaseStats);
        addColumnSettingFloat("Efficiency", block::getIrradiatorEfficiency, block::setIrradiatorEfficiency);
        addColumnSettingFloat("Heat", block::getIrradiatorHeat, block::setIrradiatorHeat);
        addColumnSectionToggle("Neutron Shield", block::isShield, block::setShield);
        addColumnSettingTexture("Closed", block::getShieldClosedTexture, block::setShieldClosedTexture);
        addColumnSubsectionToggle("Has base stats", block::isShieldHasBaseStats, block::setShieldHasBaseStats);
        addColumnSettingInt("Heat per Flux", block::getShieldHeat, block::setShieldHeat);
        addColumnSettingFloat("Efficiency", block::getShieldEfficiency, block::setShieldEfficiency);
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
                    gui, this, configuration, rule,Core.configuration.overhaul.fissionMSR,
                    multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.values()
            ));
        }, (list)->{
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
                list.add(new MenuComponentPlacementRule(rule, ()->{//edit
                    gui.open(new MenuPlacementRuleConfiguration(gui, this, configuration, rule, Core.configuration.overhaul.fissionMSR, PlacementRule.BlockType.values()));
                }, ()->{//delete
                    block.rules.remove(rule);
                    refresh();
                }));
            }
        });
        this.block = block;
    }
    @Override
    public void onGUIClosed(){
        if(block.allRecipes.isEmpty())block.port = null;
        else if(block.port==null)block.port = new Block("nuclearcraft:port_name");
        super.onGUIClosed();
    }
}