package net.ncplanner.plannerator.planner.menu.configuration;
import java.util.function.Supplier;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.AddonConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentInternalAddon extends MenuComponent{
    public final Supplier<AddonConfiguration> addon;
    double textInset = 4;
    public final MenuComponentMinimalistButton add;
    private final AddonConfiguration actualAddon;
    public MenuComponentInternalAddon(Supplier<AddonConfiguration> addon, AddonConfiguration actualAddon){
        super(0, 0, 0, 48);
        this.addon = addon;
        this.actualAddon = actualAddon;
        add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add", true, true, true).setTooltip("Load this addon"));
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        add.width = height*3;
        add.x = width-height*3/2-add.width-height;
        add.y = 0;
        add.height = height;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        if(isMouseOver)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
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
        drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, str);
    }
}