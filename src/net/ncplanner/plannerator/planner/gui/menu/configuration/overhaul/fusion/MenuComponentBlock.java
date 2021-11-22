package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block;
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
            renderer.fillTri(x+width*.25f, y+height*.75f,
                    x+width*.375f, y+height*.75f,
                    x+width*.25f, y+height*.625f);
            renderer.fillQuad(x+width*.4f, y+height*.725f,
                    x+width*.275f, y+height*.6f,
                    x+width*.5f, y+height*.375f,
                    x+width*.625f, y+height*.5f);
            renderer.fillQuad(x+width*.525f, y+height*.35f,
                    x+width*.65f, y+height*.475f,
                    x+width*.75f, y+height*.375f,
                    x+width*.625f, y+height*.25f);
        }
    }.setTooltip("Modify block"));
    public final Button delete = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillQuad(x+width*.1f, y+height*.8f,
                    x+width*.2f, y+height*.9f,
                    x+width*.9f, y+height*.2f,
                    x+width*.8f, y+height*.1f);
            renderer.fillQuad(x+width*.1f, y+height*.2f,
                    x+width*.2f, y+height*.1f,
                    x+width*.9f, y+height*.8f,
                    x+width*.8f, y+height*.9f);
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
            renderer.drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}