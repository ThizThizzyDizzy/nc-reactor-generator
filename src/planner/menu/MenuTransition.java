package planner.menu;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuTransition extends Menu{
    private final Menu from;
    private final Menu to;
    private final Transition transition;
    private final int time;
    private int timer = 0;
    public MenuTransition(GUI gui, Menu from, Menu to, Transition transition, int time){
        super(gui, from);
        this.from = from;
        this.to = to;
        this.transition = transition;
        this.time = time;
    }
    @Override
    public void tick(){
        from.tick();
        to.tick();
        timer++;
        if(time==timer){
            gui.open(to);
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        double ratio = ((timer+millisSinceLastTick/50d)/time);
        transition.render(from, to, ratio, millisSinceLastTick);
    }
    public static interface Transition{
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick);
    }
    public static class SlideTransition implements Transition{
        private final int xDiff;
        private final int yDiff;
        public SlideTransition(int xDiff, int yDiff){
            this.xDiff = xDiff;
            this.yDiff = yDiff;
        }
        @Override
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick){
            double xOff = Display.getWidth()*(1-ratio)*xDiff;
            double yOff = Display.getHeight()*(1-ratio)*yDiff;
            from.render(millisSinceLastTick);
            GL11.glPushMatrix();
            GL11.glTranslated(xOff, yOff, 0);
            GL11.glColor4d(40/255d,50/255d,100/255d, 1);
            drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
            GL11.glColor4d(1, 1, 1, 1);
            to.render(millisSinceLastTick);
            GL11.glPopMatrix();
        }
    }
}