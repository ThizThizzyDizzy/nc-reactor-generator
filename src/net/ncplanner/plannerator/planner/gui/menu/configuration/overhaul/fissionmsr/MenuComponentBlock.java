package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentBlock extends Component{
    public final Block block;
    public final Button edit = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify block"));
    public final Button delete = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("delete", x, y, width, height);
        }
    }.setTooltip("Delete block"));
    public MenuComponentBlock(Block block, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.block = block;
        edit.addAction(onEditPressed);
        delete.addAction(onDeletePressed);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        delete.x = width-height/2-height/4;
        edit.x = delete.x - height;
        delete.y = edit.y = height/4;
        delete.width = delete.height = edit.width = edit.height = height/2;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        renderer.fillRect(x, y, x+width, y+height);
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(block.texture!=null)renderer.drawImage(block.displayTexture, x, y, x+height, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.getDisplayName());
        if(block.cluster)strs.add("Can Cluster");
        if(block.createCluster)strs.add("Creates Cluster");
        if(block.conductor)strs.add("Conductor");
        if(block.casing)strs.add("Casing");
        if(block.casingEdge)strs.add("Casing Edge");
        if(block.controller)strs.add("Controller");
        if(block.heater){
            if(block.heaterHasBaseStats)strs.add("Heater Cooling: "+block.heaterCooling+" H/t");
            else strs.add("Heater");
        }
        if(block.fuelVessel){
            if(block.fuelVesselHasBaseStats){
                strs.add("Fuel Vessel Efficiency: "+block.fuelVesselEfficiency);
                strs.add("Fuel Vessel Heat: "+block.fuelVesselHeat);
                strs.add("Fuel Vessel Criticality: "+block.fuelVesselCriticality);
                if(block.fuelVesselSelfPriming)strs.add("Fuel Vessel Self-Priming");
            }else strs.add("Fuel Vessel");
        }
        if(block.reflector){
            if(block.reflectorHasBaseStats){
                strs.add("Reflector Efficiency: "+block.reflectorEfficiency);
                strs.add("Reflector Reflectivity: "+block.reflectorReflectivity);
            }else strs.add("Reflector");
        }
        if(block.irradiator){
            if(block.irradiatorHasBaseStats){
                strs.add("Irradiator Efficiency: "+block.irradiatorEfficiency);
                strs.add("Irradiator Heat: "+block.irradiatorHeat);
            }else strs.add("Irradiator");
        }
        if(block.moderator){
            if(block.moderatorHasBaseStats){
                strs.add("Moderator Flux: "+block.moderatorFlux);
                strs.add("Moderator Efficiency: "+block.moderatorEfficiency);
                if(block.moderatorActive)strs.add("Moderator Active");
            }else strs.add("Moderator");
        }
        if(block.shield){
            if(block.shieldHasBaseStats){
                strs.add("Shield Efficiency: "+block.shieldEfficiency);
                strs.add("Shield Heat: "+block.shieldHeat);
            }else strs.add("Shield");
        }
        if(block.source){
            strs.add("Source Efficiency: "+block.sourceEfficiency);
        }
        if(block.blocksLOS)strs.add("Blocks Line of Sight");
        if(block.functional)strs.add("Functional");
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            renderer.drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}