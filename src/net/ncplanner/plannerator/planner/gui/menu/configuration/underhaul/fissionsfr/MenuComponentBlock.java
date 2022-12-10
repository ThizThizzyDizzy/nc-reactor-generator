package net.ncplanner.plannerator.planner.gui.menu.configuration.underhaul.fissionsfr;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentBlock extends Component{
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
    public final Button delete = add(new Button("", true, true){
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
        renderer.drawText(x+height, y, x+width, y+height/5, block.getDisplayName());
        if(block.cooling!=0)renderer.drawText(x+height, y+height/5, x+width, y+height/5*2, "Cooling: "+block.cooling+" H/t");
        if(block.fuelCell)renderer.drawText(x+height, y+height/5*2, x+width, y+height/5*3, "Fuel Cell");
        if(block.moderator)renderer.drawText(x+height, y+height/5*3, x+width, y+height/5*4, "Moderator");
        if(block.active!=null)renderer.drawText(x+height, y+height/5*4, x+width, y+height/5*5, "Active: "+block.active);
    }
}