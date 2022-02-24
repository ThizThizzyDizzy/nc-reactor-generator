package net.ncplanner.plannerator.planner.gui.menu.component.tutorial;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
public class MenuComponentTutorialDisplay extends Component{
    private static final float fac = .01f;
    public Tutorial tutorial;
    private float tick = 0;
    public MenuComponentTutorialDisplay(Tutorial tutorial){
        super(0, 0, 0, 0);
        this.tutorial = tutorial;
    }
    @Override
    public void render2d(double deltaTime){
        int last = (int)tick;
        tick+=deltaTime*20;
        if((int)tick>last)tutorial.tick((int)tick);//TODO make it all go based on the render method
        super.render2d(deltaTime);
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getTutorialBackgroundColor());
        renderer.fillRect(0, 0, width, height);
        tutorial.preRender();
        renderer.translate(0, 0, width, width);
        tutorial.render((float)(Math.cos(2*Math.PI*tick*fac)+1)/2, tick);
        renderer.unTranslate();
    }
}