package planner.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.configuration.overhaul.fissionmsr.Block;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentBlockConfiguration extends MenuComponent{
    public final Block block;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(foregroundColor);
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2d(x+width*.25, y+height*.75);
            GL11.glVertex2d(x+width*.375, y+height*.75);
            GL11.glVertex2d(x+width*.25, y+height*.625);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d(x+width*.4, y+height*.725);
            GL11.glVertex2d(x+width*.275, y+height*.6);
            GL11.glVertex2d(x+width*.5, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.5);

            GL11.glVertex2d(x+width*.525, y+height*.35);
            GL11.glVertex2d(x+width*.65, y+height*.475);
            GL11.glVertex2d(x+width*.75, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.25);
            GL11.glEnd();
        }
    });
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(foregroundColor);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d(x+width*.1, y+height*.8);
            GL11.glVertex2d(x+width*.2, y+height*.9);
            GL11.glVertex2d(x+width*.9, y+height*.2);
            GL11.glVertex2d(x+width*.8, y+height*.1);
            
            GL11.glVertex2d(x+width*.1, y+height*.2);
            GL11.glVertex2d(x+width*.2, y+height*.1);
            GL11.glVertex2d(x+width*.9, y+height*.8);
            GL11.glVertex2d(x+width*.8, y+height*.9);
            GL11.glEnd();
        }
    });
    public MenuComponentBlockConfiguration(Block block){
        super(0, 0, 0, 150);
        color = Core.theme.getButtonColor();
        selectedColor = Core.theme.getSelectedMultiblockColor();
        foregroundColor = Core.theme.getTextColor();
        this.block = block;
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        delete.x = width-height/2-height/4;
        edit.x = delete.x - height;
        delete.y = edit.y = height/4;
        delete.width = delete.height = edit.width = edit.height = height/2;
    }
    @Override
    public void render(){
        if(isMouseOver)Core.applyColor(selectedColor);
        else Core.applyColor(color);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyColor(foregroundColor);
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.name);
        if(block.cooling>0)strs.add("Cooling: "+block.cooling+" H/t");
        if(block.cluster)strs.add("Can Cluster");
        if(block.createCluster)strs.add("Creates Cluster");
        if(block.conductor)strs.add("Conductor");
        if(block.fuelVessel)strs.add("Fuel Vessel");
        if(block.reflector)strs.add("Reflector");
        if(block.irradiator)strs.add("Irradiator");
        if(block.moderator)strs.add("Moderator");
        if(block.activeModerator)strs.add("Active Moderator");
        if(block.shield)strs.add("Neutron Shield");
        if(block.flux>0)strs.add("Neutron Flux: "+block.flux);
        if(block.moderator||block.shield||block.reflector)strs.add("Efficiency: "+block.efficiency);
        if(block.reflector)strs.add("Reflectivity: "+block.reflectivity);
        if(block.shield)strs.add("Heat Multiplier: "+block.heatMult);
        if(block.blocksLOS)strs.add("Blocks Line of Sight");
        if(block.functional)strs.add("Functional");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            drawText(x, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}