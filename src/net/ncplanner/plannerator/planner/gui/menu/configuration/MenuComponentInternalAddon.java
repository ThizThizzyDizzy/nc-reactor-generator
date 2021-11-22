package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.AddonConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentInternalAddon extends Component{
    public final Supplier<AddonConfiguration> addon;
    float textInset = 4;
    public final Button add;
    private final AddonConfiguration actualAddon;
    public MenuComponentInternalAddon(Supplier<AddonConfiguration> addon, AddonConfiguration actualAddon, Runnable addAction){
        super(0, 0, 0, 48);
        this.addon = addon;
        this.actualAddon = actualAddon;
        add = add(new Button(0, 0, 0, 0, "Add", true, true).setTooltip("Load this addon"));
        add.addAction(addAction);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        add.width = height*3;
        add.x = width-height*3/2-add.width-height;
        add.y = 0;
        add.height = height;
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
        if(actualAddon.overhaulVersion==null&&actualAddon.underhaulVersion==null){
            str = actualAddon.name;
        }else if(actualAddon.overhaulVersion!=null&&actualAddon.underhaulVersion!=null){
            str = actualAddon.name+" "+actualAddon.overhaulVersion+" | "+actualAddon.underhaulVersion;
        }else{
            str = actualAddon.name+" "+(actualAddon.overhaulVersion==null?actualAddon.underhaulVersion:actualAddon.overhaulVersion);
        }
        renderer.drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, str);
    }
}