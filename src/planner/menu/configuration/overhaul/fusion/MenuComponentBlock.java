package planner.menu.configuration.overhaul.fusion;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fusion.Block;
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
//    public boolean reflector = false;
//    public boolean reflectorHasBaseStats;
//    public float reflectorEfficiency;
        if(block.cluster)strs.add("Can Cluster");
        if(block.createCluster)strs.add("Creates Cluster");
        if(block.conductor)strs.add("Conductor");
        if(block.connector)strs.add("Connector");
        if(block.core)strs.add("Core");
        if(block.electromagnet)strs.add("Electromagnet");
        if(block.heatsink){
            if(block.heatsinkHasBaseStats)strs.add("Heatsink Cooling: "+block.heatsinkCooling+" H/t");
            else strs.add("Heatsink");
        }
        if(block.heatingBlanket){
            strs.add("Heating Blanket");
        }
        if(block.reflector){
            if(block.reflectorHasBaseStats){
                strs.add("Reflector Efficiency: "+block.reflectorEfficiency);
            }else strs.add("Reflector");
        }
        if(block.breedingBlanket){
            if(block.breedingBlanketHasBaseStats){
                strs.add("Breeding Blanket Efficiency: "+block.breedingBlanketEfficiency);
                strs.add("Breeding Blanket Heat: "+block.breedingBlanketHeat);
                if(block.breedingBlanketAugmented)strs.add("Breeding Blanket Augmented");
            }else strs.add("Breeding Blanket");
        }
        if(block.shielding){
            if(block.shieldingHasBaseStats){
                strs.add("Shielding Shieldiness: "+block.shieldingShieldiness);
            }else strs.add("Shielding");
        }
        if(block.reflector){
            if(block.reflectorHasBaseStats){
                strs.add("Reflector Efficiency: "+block.reflectorEfficiency);
            }else strs.add("Reflector");
        }
        if(block.functional)strs.add("Functional");
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}