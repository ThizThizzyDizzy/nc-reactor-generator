package planner.menu.configuration.overhaul.fissionsfr;
import java.awt.Color;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import planner.configuration.overhaul.fissionsfr.Block;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentBlockConfiguration extends MenuComponent{
    public final Block block;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, .55f){
        @Override
        public void renderForeground(){
            super.renderForeground();
            GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255f);
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
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, .55f){
        @Override
        public void renderForeground(){
            super.renderForeground();
            GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255f);
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
        color = new Color(.3f, .3f, .6f, 1f);
        selectedColor = new Color(.35f, .35f, .7f, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
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
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255f);
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.name);
        if(block.cooling>0)strs.add("Cooling: "+block.cooling+" H/t");
        if(block.cluster)strs.add("Can Cluster");
        if(block.createCluster)strs.add("Creates Cluster");
        if(block.conductor)strs.add("Conductor");
        if(block.fuelCell)strs.add("Fuel Cell");
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