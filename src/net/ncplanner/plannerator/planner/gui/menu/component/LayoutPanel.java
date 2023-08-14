package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.Layout;
public class LayoutPanel extends Component{
    private Supplier<Color> background = null;
    private final Layout layout;
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
    public void drawBackground(double deltaTime){
        if(background!=null){
            Renderer renderer = new Renderer();
            renderer.setColor(background.get());
            renderer.fillRect(x, y, width, height);
        }
    }
    public LayoutPanel setBackgroundColor(Supplier<Color> color){
        background = color;
        return this;
    }
}