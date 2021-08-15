package planner.menu.configuration.overhaul.turbine;
import java.util.ArrayList;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.Block;
import multiblock.configuration.overhaul.turbine.PlacementRule;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentTextureButton;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.MenuComponentPlacementRule;
import planner.menu.configuration.MenuPlacementRuleConfiguration;
import planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBlockConfiguration extends PartConfigurationMenu{
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        addMainSection(null, block::getTexture, block::setTexture, "The ingame name of this block. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of this block.", ()->{return block.name;}, ()->{return block.displayName;}, ()->{return block.legacyNames;}, block::setName, block::setDisplayName, block::setLegacyNames);
        addSettingBoolean("Bearing", block::isBearing, block::setBearing);
        addSettingBoolean("Connector", block::isConnector, block::setConnector);
        addSettingBoolean("Casing", block::isCasing, block::setCasing);
        addSettingBoolean("Inlet", block::isInlet, block::setInlet);
        finishSettingRow();
        addSettingBoolean("Shaft", block::isShaft, block::setShaft);
        addSettingBoolean("Controller", block::isController, block::setController);
        addSettingBoolean("Casing Edge", block::isCasingEdge, block::setCasingEdge);
        addSettingBoolean("Outlet", block::isOutlet, block::setOutlet);
        addColumnSectionToggle("Blade", block::isBlade, block::setBlade);
        addColumnSettingFloat("Efficiency", block::getBladeEfficiency, block::setBladeEfficiency);
        addColumnSettingFloat("Expansion", block::getBladeExpansion, block::setBladeExpansion);
        addColumnSettingBoolean("Stator", block::isBladeStator, block::setBladeStator);
        finishColumn();
        addColumnSectionToggle("Coil", block::isCoil, block::setCoil);
        addColumnSettingFloat("Efficiency", block::getCoilEfficiency, block::setCoilEfficiency);
        addList(()->{return "Placement Rules ("+block.rules.size()+")";}, "New Rule", ()->{
            PlacementRule rule;
            block.rules.add(rule = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(
                    gui, this, configuration, rule,Core.configuration.overhaul.turbine,
                    multiblock.configuration.overhaul.turbine.PlacementRule.BlockType.values()
            ));
        }, (list)->{
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
                list.add(new MenuComponentPlacementRule(rule, ()->{//edit
                    gui.open(new MenuPlacementRuleConfiguration(gui, this, configuration, rule, Core.configuration.overhaul.turbine, PlacementRule.BlockType.values()));
                }, ()->{//delete
                    block.rules.remove(rule);
                    refresh();
                }));
            }
        });
    }
}