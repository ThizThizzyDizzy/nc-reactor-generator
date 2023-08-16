package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.gui.Component;
public class Panel extends Component{
    private Image image;
    public Panel(){}
    public Panel(float x, float y, float width, float height){
        super(x, y, width, height);
    }
    private Supplier<Color> background = null;
    @Override
    public void drawBackground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(background!=null){
            renderer.setColor(background.get());
            renderer.fillRect(x, y, x+width, y+height);
        }
        if(image!=null){
            renderer.drawImage(image, x, y, x+width, y+height);
        }
    }
    public Panel setBackgroundColor(Supplier<Color> color){
        background = color;
        return this;
    }
    public Panel setImage(Image image){
        this.image = image;
        return this;
    }
}