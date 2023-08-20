package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MenuComponentPlacementRule;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MenuPlacementRuleConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.PartConfigurationMenu;
public class MenuBlockConfiguration extends PartConfigurationMenu{
    private final Block block;
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        addPortSection(()->{return block.port==null?null:block.port.texture;}, (t)->{if(block.port!=null)block.port.setTexture(t);}, ()->{return block.port==null?null:block.port.portOutputTexture;}, (t)->{if(block.port!=null)block.port.setPortOutputTexture(t);}, "The ingame name of this block's access port. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of the port in input mode.", "The user-friendly name of the port in output mode.", ()->{return block.port==null?"":block.port.name;}, ()->{return block.port==null?null:block.port.displayName;}, ()->{return block.port==null?null:block.port.portOutputDisplayName;}, (s)->{if(block.port!=null)block.port.setName(s);}, (s)->{if(block.port!=null)block.port.setDisplayName(s);}, (s)->{if(block.port!=null)block.port.setPortOutputDisplayName(s);}, ()->{return block.port!=null;});
        addColumnSettingTexture("Vent Output", block::getCoolantVentOutputTexture, block::setCoolantVentOutputTexture);
        addColumnSettingTexture("Closed", block::getShieldClosedTexture, block::setShieldClosedTexture);
        this.block = block;
    }
    @Override
    public void onClosed(){
        if(block.allRecipes.isEmpty())block.port = null;
        else if(block.port==null)block.port = new Block("nuclearcraft:port_name");
        super.onClosed();
    }
    @Override
    public void doRefresh(){
        if(block.allRecipes.isEmpty())block.port = null;
        else if(block.port==null)block.port = new Block("nuclearcraft:port_name");
        super.doRefresh();
    }
}