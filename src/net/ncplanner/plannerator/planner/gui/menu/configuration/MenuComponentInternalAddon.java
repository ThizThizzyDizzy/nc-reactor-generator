package net.ncplanner.plannerator.planner.gui.menu.configuration;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.ncpf.Addon;
public class MenuComponentInternalAddon extends Component{
    float textInset = 4;
    public final Button add;
    private final Addon addon;
    public MenuComponentInternalAddon(Addon addon, Runnable addAction){
        super(0, 0, 0, 48);
        this.addon = addon;
        add = add(new Button("Add", true, true).setTooltip("Load this addon"));
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
        renderer.drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, addon.getName());
    }
}