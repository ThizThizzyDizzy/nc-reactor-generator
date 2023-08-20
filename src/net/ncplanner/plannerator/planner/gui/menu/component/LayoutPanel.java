package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.Layout;
public class LayoutPanel extends Panel{
    public final Layout layout;
    public LayoutPanel(Layout layout){
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
    @Override
    public LayoutPanel setBackgroundColor(Supplier<Color> color){
        return (LayoutPanel)super.setBackgroundColor(color);
    }
}