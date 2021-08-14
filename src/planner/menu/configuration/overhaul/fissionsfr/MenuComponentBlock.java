package planner.menu.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionsfr.Block;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentBlock extends MenuComponent{
    public final Block block;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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
        @Override
        public void action(){
            onEditPressed.run();
        }
    }.setTooltip("Modify block"));
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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
        @Override
        public void action(){
            onDeletePressed.run();
        }
    }.setTooltip("Delete block"));
    private final Runnable onEditPressed;
    private final Runnable onDeletePressed;
    public MenuComponentBlock(Block block, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.block = block;
        this.onEditPressed = onEditPressed;
        this.onDeletePressed = onDeletePressed;
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
        if(isMouseOver)Core.applyColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyWhite();
        if(block.texture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(block.displayTexture));
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.getDisplayName());
        if(block.cluster)strs.add("Can Cluster");
        if(block.createCluster)strs.add("Creates Cluster");
        if(block.conductor)strs.add("Conductor");
        if(block.casing)strs.add("Casing");
        if(block.casingEdge)strs.add("Casing Edge");
        if(block.controller)strs.add("Controller");
        if(block.heatsink){
            if(block.heatsinkHasBaseStats)strs.add("Heatsink Cooling: "+block.heatsinkCooling+" H/t");
            else strs.add("Heatsink");
        }
        if(block.fuelCell){
            if(block.fuelCellHasBaseStats){
                strs.add("Fuel Cell Efficiency: "+block.fuelCellEfficiency);
                strs.add("Fuel Cell Heat: "+block.fuelCellHeat);
                strs.add("Fuel Cell Criticality: "+block.fuelCellCriticality);
                if(block.fuelCellSelfPriming)strs.add("Fuel Cell Self-Priming");
            }else strs.add("Fuel Cell");
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
        if(block.coolantVent)strs.add("Coolant Vent");
        if(block.blocksLOS)strs.add("Blocks Line of Sight");
        if(block.functional)strs.add("Functional");
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}