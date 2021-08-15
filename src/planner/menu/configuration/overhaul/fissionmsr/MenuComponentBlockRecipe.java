package planner.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentBlockRecipe extends MenuComponent{
    private final Block block;
    public final BlockRecipe blockRecipe;
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
    }.setTooltip("Modify block recipe"));
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
    }.setTooltip("Delete block recipe"));
    private final Runnable onEditPressed;
    private final Runnable onDeletePressed;
    public MenuComponentBlockRecipe(Block block, BlockRecipe blockRecipe, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.block = block;
        this.blockRecipe = blockRecipe;
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
        if(blockRecipe.inputTexture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(blockRecipe.inputDisplayTexture));
        if(blockRecipe.outputTexture!=null)drawRect(x+height, y, x+height*2, y+height, Core.getTexture(blockRecipe.outputDisplayTexture));
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(blockRecipe.getInputDisplayName());
        if(block.heater){
            if(block.heaterHasBaseStats)strs.add("Heater Cooling: "+block.heaterCooling+" H/t");
            else strs.add("Heater");
        }
        if(block.fuelVessel){
            strs.add("Fuel Efficiency: "+blockRecipe.fuelVesselEfficiency);
            strs.add("Fuel Heat: "+blockRecipe.fuelVesselHeat);
            strs.add("Fuel Criticality: "+blockRecipe.fuelVesselCriticality);
            if(blockRecipe.fuelVesselSelfPriming)strs.add("Fuel Self-Priming");
        }
        if(block.reflector){
            strs.add("Reflector Efficiency: "+blockRecipe.reflectorEfficiency);
            strs.add("Reflector Reflectivity: "+blockRecipe.reflectorReflectivity);
        }
        if(block.irradiator){
            strs.add("Irradiator Efficiency: "+blockRecipe.irradiatorEfficiency);
            strs.add("Irradiator Heat: "+blockRecipe.irradiatorHeat);
        }
        if(block.moderator){
            strs.add("Moderator Flux: "+blockRecipe.moderatorFlux);
            strs.add("Moderator Efficiency: "+blockRecipe.moderatorEfficiency);
            if(blockRecipe.moderatorActive)strs.add("Moderator Active");
        }
        if(block.shield){
            strs.add("Shield Efficiency: "+blockRecipe.shieldEfficiency);
            strs.add("Shield Heat: "+blockRecipe.shieldHeat);
        }
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            drawText(x+height*2, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
//        drawText(x+height*2, y+height/4, x+width, y+height/4*2, blockRecipe.getOutputDisplayName());
    }
}
