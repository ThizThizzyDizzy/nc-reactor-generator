package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentAddon extends Component{
    public final Configuration addon;
    float textInset = 4;
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
    }.setTooltip("Modify addon"));
    public final Button remove;
    public MenuComponentAddon(Configuration addon, Runnable editAction, Runnable removeAction){
        super(0, 0, 0, 48);
        this.addon = addon;
        remove = add(new Button(0, 0, 0, 0, "Remove", true, true).setTooltip("Remove or Delete Addon"));
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