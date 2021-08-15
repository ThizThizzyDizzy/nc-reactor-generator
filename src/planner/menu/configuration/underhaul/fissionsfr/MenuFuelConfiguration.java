package planner.menu.configuration.underhaul.fissionsfr;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFuelConfiguration extends PartConfigurationMenu{
    public MenuFuelConfiguration(GUI gui, Menu parent, Configuration configuration, Fuel fuel){
        super(gui, parent, configuration, fuel.getDisplayName());
        addMainSection(null, fuel::getTexture, fuel::setTexture, "The ingame name of this fuel. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of this fuel.", ()->{return fuel.name;}, ()->{return fuel.displayName;}, ()->{return fuel.legacyNames;}, fuel::setName, fuel::setDisplayName, fuel::setLegacyNames);
        addSettingFloat("Power", fuel::getPower, fuel::setPower);
        addSettingFloat("Heat", fuel::getHeat, fuel::setHeat);
        addSettingInt("Time", fuel::getTime, fuel::setTime);
        finishSettingRow();
    }
}