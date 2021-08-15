package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Block;
import multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import planner.Core;
import planner.menu.configuration.MenuComponentPlacementRule;
import planner.menu.configuration.MenuPlacementRuleConfiguration;
import planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockConfiguration extends PartConfigurationMenu{
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        addMainSection(null, block::getTexture, block::setTexture, "The ingame name of this block. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of this block.", ()->{return block.name;}, ()->{return block.displayName;}, ()->{return block.legacyNames;}, block::setName, block::setDisplayName, block::setLegacyNames);
        addSettingBoolean("Fuel Cell", block::isFuelCell, block::setFuelCell);
        addSettingBoolean("Moderator", block::isModerator, block::setModerator);
        addSettingInt("Cooling", block::getCooling, block::setCooling);
        finishSettingRow();
        addSettingBoolean("Casing", block::isCasing, block::setCasing);
        addSettingBoolean("Controller", block::isController, block::setController);
        addSetting("Active Coolant", block::getActive, (s)->{block.setActive(s.isEmpty()?null:s);}).setTooltip("If set, this block is an active cooler\nThis is the fluid it takes an an input");
        finishSettingRow();
        addList(()->{return "Placement Rules ("+block.rules.size()+")";}, "New Rule", ()->{
            PlacementRule rule;
            block.rules.add(rule = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(
                    gui, this, configuration, rule,Core.configuration.underhaul.fissionSFR,
                    multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.values()
            ));
        }, (list)->{
            for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
                list.add(new MenuComponentPlacementRule((PlacementRule) rule, ()->{//edit
                    gui.open(new MenuPlacementRuleConfiguration(gui, this, configuration, rule, Core.configuration.underhaul.fissionSFR, PlacementRule.BlockType.values()));
                }, ()->{//delete
                    block.rules.remove(rule);
                    refresh();
                }));
            }
        });
    }
}