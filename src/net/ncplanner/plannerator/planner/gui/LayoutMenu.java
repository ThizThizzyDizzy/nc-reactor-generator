package net.ncplanner.plannerator.planner.gui;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.Layout;
public class LayoutMenu extends Menu{
    public final Layout layout;
    public LayoutMenu(GUI gui, Layout layout){
        super(gui);
        this.layout = super.add(layout);
    }
    public LayoutMenu(Menu parent, Layout layout){
        super(parent);
        this.layout = super.add(layout);
    }
    @Override
    public <T extends Component> T add(T component){
        return layout.add(component);
    }
    @Override
    public void render2d(double deltaTime){
        layout.x = layout.y = 0;
        layout.width = width;
        layout.height = height;
        layout.arrangeComponents();
        super.render2d(deltaTime);
    }
}