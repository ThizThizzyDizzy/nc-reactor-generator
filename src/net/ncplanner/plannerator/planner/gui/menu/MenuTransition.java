package net.ncplanner.plannerator.planner.gui.menu;
import java.util.HashMap;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuError;
public class MenuTransition extends Menu{
    private final Menu from;
    private final Menu to;
    private final Transition transition;
    private final int time;
    private float timer = 0;
    public MenuTransition(GUI gui, Menu from, Menu to, Transition transition, int time){
        super(gui, from);
        this.from = from;
        this.to = to;
        this.transition = transition;
        this.time = time;
    }
    @Override
    public void onOpened(){
        to.width = width;
        to.height = height;
        to.onOpened();
    }
    @Override
    public void render2d(double deltaTime){
        timer+=deltaTime*20;
        float ratio = Math.max(0, Math.min(1, timer/time));
        transition.render(from, to, ratio, deltaTime);
        if(timer>=time){
            transition.finalCheck(from, to);
            Menu dialog = null;
            Menu baseDialog = null;
            if(gui.menu instanceof MenuDialog){
                dialog = baseDialog = gui.menu;
                while(baseDialog.parent instanceof MenuDialog)baseDialog = baseDialog.parent;
            }
            try{
                gui.open(to);
            }catch(Exception ex){
                dialog = new MenuError(gui, dialog, "Error opening menu!", ex);
            }
            if(baseDialog!=null){
                baseDialog.parent = gui.menu;
                gui.menu = dialog;
            }
        }
    }
    public static interface Transition{
        public void render(Menu from, Menu to, float ratio, double deltaTime);
        public void finalCheck(Menu from, Menu to);
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
        public void render(Menu from, Menu to, float ratio, double deltaTime){
            Renderer renderer = new Renderer();
            float xOff = from.gui.getWidth()*(slideTo?ratio:(1-ratio))*xDiff;
            float yOff = from.gui.getHeight()*(slideTo?ratio:(1-ratio))*yDiff;
            if(slideTo){
                to.render2d(deltaTime);
                renderer.translate(xOff, yOff);
                renderer.setColor(Core.theme.getMenuBackgroundColor());
                renderer.fillRect(0, 0, from.gui.getWidth(), from.gui.getHeight());
                renderer.setWhite();
                from.render2d(deltaTime);
                renderer.unTranslate();
            }else{
                from.render2d(deltaTime);
                renderer.translate(xOff, yOff);
                renderer.setColor(Core.theme.getMenuBackgroundColor());
                renderer.fillRect(0, 0, from.gui.getWidth(), from.gui.getHeight());
                renderer.setWhite();
                to.render2d(deltaTime);
                renderer.unTranslate();
            }
        }
        @Override
        public void finalCheck(Menu from, Menu to){}
    }
    public static class SplitTransitionX implements Transition{
        public static SplitTransitionX slideOut(float dividerX){
            return new SplitTransitionX(dividerX, true);
        }
        public static SplitTransitionX slideIn(float dividerX){
            return new SplitTransitionX(dividerX, false);
        }
        private final float dividerX;
        private final boolean slideOut;
        public SplitTransitionX(float dividerX, boolean slideOut){
            this.dividerX = dividerX;
            this.slideOut = slideOut;
        }
        private final HashMap<Component, Float> initialX = new HashMap<>();
        @Override
        public void render(Menu from, Menu to, float ratio, double deltaTime){
            Renderer renderer = new Renderer();
            if(slideOut){
                if(initialX.isEmpty()){
                    from.render2d(deltaTime);
                    from.render2d(deltaTime);
                    for(Component c : from.components){
                        initialX.put(c, c.x);
                    }
                }
                to.render2d(deltaTime);
                float totalLeft = 0;
                float totalRight = 0;
                for(Component c : from.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, from.gui.getWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(Component c : from.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        c.x = initialX+(totalRight)*ratio;
                    }else{
                        c.x = initialX-(totalLeft)*ratio;
                    }
                }
                renderer.setColor(Core.theme.getMenuBackgroundColor());
                renderer.fillRect(0, 0, (1-ratio)*from.gui.getWidth()*dividerX, from.gui.getHeight());
                renderer.fillRect(from.gui.getWidth()-(1-ratio)*from.gui.getWidth()*(1-dividerX), 0, from.gui.getWidth(), from.gui.getHeight());
                for(Component component : from.components){
                    component.render2d(deltaTime);
                }
                from.drawForeground(deltaTime);
            }else{
                if(initialX.isEmpty()){
                    to.render2d(deltaTime);
                    to.render2d(deltaTime);
                    for(Component c : to.components){
                        initialX.put(c, c.x);
                    }
                }
                from.render2d(deltaTime);
                float totalLeft = 0;
                float totalRight = 0;
                for(Component c : to.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, from.gui.getWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(Component c : to.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        c.x = initialX+(totalRight)*(1-ratio);
                    }else{
                        c.x = initialX-(totalLeft)*(1-ratio);
                    }
                }
                renderer.setColor(Core.theme.getMenuBackgroundColor());
                renderer.fillRect(0, 0, (ratio)*from.gui.getWidth()*dividerX, from.gui.getHeight());
                renderer.fillRect(from.gui.getWidth()-(ratio)*from.gui.getWidth()*(1-dividerX), 0, from.gui.getWidth(), from.gui.getHeight());
                for(Component component : to.components){
                    component.render2d(deltaTime);
                }
                to.drawForeground(deltaTime);
            }
        }
        @Override
        public void finalCheck(Menu from, Menu to){
            if(slideOut){
                float totalLeft = 0;
                float totalRight = 0;
                for(Component c : from.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, from.gui.getWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(Component c : from.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        c.x = initialX+(totalRight);
                    }else{
                        c.x = initialX-(totalLeft);
                    }
                }
            }else{
                float totalLeft = 0;
                float totalRight = 0;
                for(Component c : to.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, from.gui.getWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(Component c : to.components){
                    float initialX = this.initialX.getOrDefault(c, c.x);
                    float x = (c.x+c.width/2)/from.gui.getWidth();
                    if(x>dividerX){
                        c.x = initialX;
                    }else{
                        c.x = initialX;
                    }
                }
            }
        }
    }
    public static class SplitTransitionY implements Transition{
        public static SplitTransitionY slideOut(float dividerY){
            return new SplitTransitionY(dividerY, true);
        }
        public static SplitTransitionY slideIn(float dividerY){
            return new SplitTransitionY(dividerY, false);
        }
        private final float dividerY;
        private final boolean slideOut;
        public SplitTransitionY(float dividerY, boolean slideOut){
            this.dividerY = dividerY;
            this.slideOut = slideOut;
        }
        private final HashMap<Component, Float> initialY = new HashMap<>();
        @Override
        public void render(Menu from, Menu to, float ratio, double deltaTime){
            Renderer renderer = new Renderer();
            if(slideOut){
                if(initialY.isEmpty()){
                    from.render2d(deltaTime);
                    from.render2d(deltaTime);
                    for(Component c : from.components){
                        initialY.put(c, c.y);
                    }
                }
                to.render2d(deltaTime);
                float totalDown = 0;
                float totalRight = 0;
                for(Component c : from.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        totalRight = Math.max(totalRight, from.gui.getHeight()-initialY);
                    }else{
                        totalDown = Math.max(totalDown, initialY+c.height);
                    }
                }
                for(Component c : from.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        c.y = initialY+(totalRight)*ratio;
                    }else{
                        c.y = initialY-(totalDown)*ratio;
                    }
                }
                renderer.setColor(Core.theme.getMenuBackgroundColor());
                renderer.fillRect(0, 0, from.gui.getWidth(), (1-ratio)*from.gui.getHeight()*dividerY);
                renderer.fillRect(0, from.gui.getHeight()-(1-ratio)*from.gui.getHeight()*(1-dividerY), from.gui.getWidth(), from.gui.getHeight());
                for(Component component : from.components){
                    component.render2d(deltaTime);
                }
                from.drawForeground(deltaTime);
            }else{
                if(initialY.isEmpty()){
                    to.render2d(deltaTime);
                    to.render2d(deltaTime);
                    for(Component c : to.components){
                        initialY.put(c, c.y);
                    }
                }
                from.render2d(deltaTime);
                float totalUp = 0;
                float totalDown = 0;
                for(Component c : to.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        totalDown = Math.max(totalDown, from.gui.getHeight()-initialY);
                    }else{
                        totalUp = Math.max(totalUp, initialY+c.height);
                    }
                }
                for(Component c : to.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        c.y = initialY+(totalDown)*(1-ratio);
                    }else{
                        c.y = initialY-(totalUp)*(1-ratio);
                    }
                }
                renderer.setColor(Core.theme.getMenuBackgroundColor());
                renderer.fillRect(0, 0, from.gui.getWidth(), (ratio)*from.gui.getHeight()*dividerY);
                renderer.fillRect(0, from.gui.getHeight()-(ratio)*from.gui.getHeight()*(1-dividerY), from.gui.getWidth(), from.gui.getHeight());
                for(Component component : to.components){
                    component.render2d(deltaTime);
                }
                to.drawForeground(deltaTime);
            }
        }
        @Override
        public void finalCheck(Menu from, Menu to){
            if(slideOut){
                float totalUp = 0;
                float totalDown = 0;
                for(Component c : from.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        totalDown = Math.max(totalDown, from.gui.getHeight()-initialY);
                    }else{
                        totalUp = Math.max(totalUp, initialY+c.height);
                    }
                }
                for(Component c : from.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        c.y = initialY+(totalDown);
                    }else{
                        c.y = initialY-(totalUp);
                    }
                }
            }else{
                float totalUp = 0;
                float totalDown = 0;
                for(Component c : to.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        totalDown = Math.max(totalDown, from.gui.getHeight()-initialY);
                    }else{
                        totalUp = Math.max(totalUp, initialY+c.height);
                    }
                }
                for(Component c : to.components){
                    float initialY = this.initialY.getOrDefault(c, c.y);
                    float y = (c.y+c.height/2)/from.gui.getHeight();
                    if(y>dividerY){
                        c.y = initialY;
                    }else{
                        c.y = initialY;
                    }
                }
            }
        }
    }
    @Override
    public void onCursorMoved(double xpos, double ypos){
        to.onCursorMoved(xpos, ypos);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        to.onMouseButton(x, y, button, action, mods);
    }
    @Override
    public boolean onScroll(double dx, double dy){
        return to.onScroll(dx, dy);
    }
    @Override
    public void onCharTyped(char c){
        to.onCharTyped(c);
    }
    @Override
    public void onFilesDropped(String[] files){
        to.onFilesDropped(files);
    }
    @Override
    public void onWindowFocusGained(){
        to.onWindowFocusGained();
    }
    @Override
    public void onWindowFocusLost(){
        to.onWindowFocusLost();
    }
}