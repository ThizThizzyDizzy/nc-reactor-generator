package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.gui.Component;
public class Panel extends Component{
    public Panel(){}
    public Panel(float x, float y, float width, float height){
        super(x, y, width, height);
    }
    private Supplier<Color> background = null;
    @Override
    public void drawBackground(double deltaTime){
        if(background!=null){
            Renderer renderer = new Renderer();
            renderer.setColor(background.get());
            renderer.fillRect(x, y, x+width, y+height);
        }
    }
    public Panel setBackgroundColor(Supplier<Color> color){
        background = color;
        return this;
    }
}