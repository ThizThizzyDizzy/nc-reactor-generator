package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentAddonBlock extends Component{
    public final Block parent;
    public final Block block;
    public final Button edit = add(new Button("", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify block"));
    public final Button delete = add(new Button("-", true, true).setTooltip("Remove block"));
    public MenuComponentAddonBlock(Block parent, Block block, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 50);
        this.parent = parent;
        this.block = block;
        edit.addAction(onEditPressed);
        delete.addAction(onDeletePressed);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        delete.x = width-height/2-height;
        edit.x = delete.x - height*2;
        delete.width = delete.height = edit.width = edit.height = height;
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
        if(parent.texture!=null)renderer.drawImage(parent.displayTexture, x, y, x+height, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(parent.getDisplayName());
        strs.add(block.recipes.size()+" Added Recipe"+(block.recipes.size()==1?"":"s"));
        while(strs.size()<2)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            renderer.drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}