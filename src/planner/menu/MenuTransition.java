package planner.menu;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
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
    public void onGUIOpened(){
        to.onGUIOpened();
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
        double ratio = Math.max(0, Math.min(1, (timer+Math.max(0,Math.min(1, millisSinceLastTick/50d)))/time));
        transition.render(from, to, ratio, millisSinceLastTick);
    }
    public static interface Transition{
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick);
    }
    public static class SlideTransition implements Transition{
        public static SlideTransition slideTo(int xDiff, int yDiff){
            return new SlideTransition(xDiff, yDiff, true);
        }
        public static SlideTransition slideFrom(int xDiff, int yDiff){
            return new SlideTransition(xDiff, yDiff, false);
        }
        private final int xDiff;
        private final int yDiff;
        private final boolean slideTo;
        private SlideTransition(int xDiff, int yDiff, boolean slideTo){
            this.xDiff = xDiff;
            this.yDiff = yDiff;
            this.slideTo = slideTo;
        }
        @Override
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick){
            double xOff = Display.getWidth()*(slideTo?ratio:(1-ratio))*xDiff;
            double yOff = Display.getHeight()*(slideTo?ratio:(1-ratio))*yDiff;
            if(slideTo){
                to.render(millisSinceLastTick);
                GL11.glPushMatrix();
                GL11.glTranslated(xOff, yOff, 0);
                GL11.glColor4d(40/255d,50/255d,100/255d, 1);
                drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
                GL11.glColor4d(1, 1, 1, 1);
                from.render(millisSinceLastTick);
                GL11.glPopMatrix();
            }else{
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
}