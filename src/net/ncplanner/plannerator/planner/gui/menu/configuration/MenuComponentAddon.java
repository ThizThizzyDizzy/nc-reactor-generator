package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentAddon extends Component{
    public final Configuration addon;
    float textInset = 4;
    public final Button edit = add(new Button("", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify addon"));
    public final Button remove;
    public MenuComponentAddon(Configuration addon, Runnable editAction, Runnable removeAction){
        super(0, 0, 0, 48);
        this.addon = addon;
        remove = add(new Button("Remove", true, true).setTooltip("Remove or Delete Addon"));
        edit.addAction(editAction);
        remove.addAction(removeAction);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        edit.x = width-height*3/2;
        remove.width = edit.width*3;
        remove.x = edit.x-remove.width-height;
        remove.y = edit.y = 0;
        remove.height = edit.width = edit.height = height;
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
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        String str;
        if(addon.overhaulVersion==null&&addon.underhaulVersion==null){
            str = addon.name;
        }else if(addon.overhaulVersion!=null&&addon.underhaulVersion!=null){
            str = addon.name+" "+addon.overhaulVersion+" | "+addon.underhaulVersion;
        }else{
            str = addon.name+" "+(addon.overhaulVersion==null?addon.underhaulVersion:addon.overhaulVersion);
        }
        renderer.drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, str);
    }
}