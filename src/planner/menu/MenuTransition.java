package planner.menu;
import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
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
            transition.finalCheck(from, to);
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
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick){
            double xOff = Core.helper.displayWidth()*(slideTo?ratio:(1-ratio))*xDiff;
            double yOff = Core.helper.displayHeight()*(slideTo?ratio:(1-ratio))*yDiff;
            if(slideTo){
                to.render(millisSinceLastTick);
                GL11.glPushMatrix();
                GL11.glTranslated(xOff, yOff, 0);
                Core.applyColor(Core.theme.getMenuBackgroundColor());
                drawRect(0, 0, Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
                Core.applyWhite();
                from.render(millisSinceLastTick);
                GL11.glPopMatrix();
            }else{
                from.render(millisSinceLastTick);
                GL11.glPushMatrix();
                GL11.glTranslated(xOff, yOff, 0);
                Core.applyColor(Core.theme.getMenuBackgroundColor());
                drawRect(0, 0, Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
                Core.applyWhite();
                to.render(millisSinceLastTick);
                GL11.glPopMatrix();
            }
        }
        @Override
        public void finalCheck(Menu from, Menu to){}
    }
    public static class SplitTransitionX implements Transition{
        public static SplitTransitionX slideOut(double dividerX){
            return new SplitTransitionX(dividerX, true);
        }
        public static SplitTransitionX slideIn(double dividerX){
            return new SplitTransitionX(dividerX, false);
        }
        private final double dividerX;
        private final boolean slideOut;
        public SplitTransitionX(double dividerX, boolean slideOut){
            this.dividerX = dividerX;
            this.slideOut = slideOut;
        }
        private final HashMap<MenuComponent, Double> initialX = new HashMap<>();
        @Override
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick){
            if(slideOut){
                if(initialX.isEmpty()){
                    GL11.glPushMatrix();
                    GL11.glScaled(0,0,0);
                    from.render(millisSinceLastTick);
                    from.render(millisSinceLastTick);
                    GL11.glPopMatrix();
                    for(MenuComponent c : from.components){
                        initialX.put(c, c.x);
                    }
                }
                to.render(millisSinceLastTick);
                double totalLeft = 0;
                double totalRight = 0;
                for(MenuComponent c : from.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, Core.helper.displayWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(MenuComponent c : from.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        c.x = initialX+(totalRight)*ratio;
                    }else{
                        c.x = initialX-(totalLeft)*ratio;
                    }
                }
                Core.applyColor(Core.theme.getMenuBackgroundColor());
                drawRect(0, 0, (1-ratio)*Core.helper.displayWidth()*dividerX, Core.helper.displayHeight(), 0);
                drawRect(Core.helper.displayWidth()-(1-ratio)*Core.helper.displayWidth()*(1-dividerX), 0, Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
                for(MenuComponent component : from.components){
                    component.render(millisSinceLastTick);
                }
                from.renderForeground();
            }else{
                if(initialX.isEmpty()){
                    GL11.glPushMatrix();
                    GL11.glScaled(0,0,0);
                    to.render(millisSinceLastTick);
                    to.render(millisSinceLastTick);
                    GL11.glPopMatrix();
                    for(MenuComponent c : to.components){
                        initialX.put(c, c.x);
                    }
                }
                from.render(millisSinceLastTick);
                double totalLeft = 0;
                double totalRight = 0;
                for(MenuComponent c : to.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, Core.helper.displayWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(MenuComponent c : to.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        c.x = initialX+(totalRight)*(1-ratio);
                    }else{
                        c.x = initialX-(totalLeft)*(1-ratio);
                    }
                }
                Core.applyColor(Core.theme.getMenuBackgroundColor());
                drawRect(0, 0, (ratio)*Core.helper.displayWidth()*dividerX, Core.helper.displayHeight(), 0);
                drawRect(Core.helper.displayWidth()-(ratio)*Core.helper.displayWidth()*(1-dividerX), 0, Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
                for(MenuComponent component : to.components){
                    component.render(millisSinceLastTick);
                }
                to.renderForeground();
            }
        }
        @Override
        public void finalCheck(Menu from, Menu to){
            if(slideOut){
                double totalLeft = 0;
                double totalRight = 0;
                for(MenuComponent c : from.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, Core.helper.displayWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(MenuComponent c : from.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        c.x = initialX+(totalRight);
                    }else{
                        c.x = initialX-(totalLeft);
                    }
                }
            }else{
                double totalLeft = 0;
                double totalRight = 0;
                for(MenuComponent c : to.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
                    if(x>dividerX){
                        totalRight = Math.max(totalRight, Core.helper.displayWidth()-initialX);
                    }else{
                        totalLeft = Math.max(totalLeft, initialX+c.width);
                    }
                }
                for(MenuComponent c : to.components){
                    double initialX = this.initialX.getOrDefault(c, c.x);
                    double x = (c.x+c.width/2)/Core.helper.displayWidth();
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
        public static SplitTransitionY slideOut(double dividerY){
            return new SplitTransitionY(dividerY, true);
        }
        public static SplitTransitionY slideIn(double dividerY){
            return new SplitTransitionY(dividerY, false);
        }
        private final double dividerY;
        private final boolean slideOut;
        public SplitTransitionY(double dividerY, boolean slideOut){
            this.dividerY = dividerY;
            this.slideOut = slideOut;
        }
        private final HashMap<MenuComponent, Double> initialY = new HashMap<>();
        @Override
        public void render(Menu from, Menu to, double ratio, int millisSinceLastTick){
            if(slideOut){
                if(initialY.isEmpty()){
                    GL11.glPushMatrix();
                    GL11.glScaled(0,0,0);
                    from.render(millisSinceLastTick);
                    from.render(millisSinceLastTick);
                    GL11.glPopMatrix();
                    for(MenuComponent c : from.components){
                        initialY.put(c, c.y);
                    }
                }
                to.render(millisSinceLastTick);
                double totalDown = 0;
                double totalRight = 0;
                for(MenuComponent c : from.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        totalRight = Math.max(totalRight, Core.helper.displayHeight()-initialY);
                    }else{
                        totalDown = Math.max(totalDown, initialY+c.height);
                    }
                }
                for(MenuComponent c : from.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        c.y = initialY+(totalRight)*ratio;
                    }else{
                        c.y = initialY-(totalDown)*ratio;
                    }
                }
                Core.applyColor(Core.theme.getMenuBackgroundColor());
                drawRect(0, 0, Core.helper.displayWidth(), (1-ratio)*Core.helper.displayHeight()*dividerY, 0);
                drawRect(0, Core.helper.displayHeight()-(1-ratio)*Core.helper.displayHeight()*(1-dividerY), Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
                for(MenuComponent component : from.components){
                    component.render(millisSinceLastTick);
                }
                from.renderForeground();
            }else{
                if(initialY.isEmpty()){
                    GL11.glPushMatrix();
                    GL11.glScaled(0,0,0);
                    to.render(millisSinceLastTick);
                    to.render(millisSinceLastTick);
                    GL11.glPopMatrix();
                    for(MenuComponent c : to.components){
                        initialY.put(c, c.y);
                    }
                }
                from.render(millisSinceLastTick);
                double totalUp = 0;
                double totalDown = 0;
                for(MenuComponent c : to.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        totalDown = Math.max(totalDown, Core.helper.displayHeight()-initialY);
                    }else{
                        totalUp = Math.max(totalUp, initialY+c.height);
                    }
                }
                for(MenuComponent c : to.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        c.y = initialY+(totalDown)*(1-ratio);
                    }else{
                        c.y = initialY-(totalUp)*(1-ratio);
                    }
                }
                Core.applyColor(Core.theme.getMenuBackgroundColor());
                drawRect(0, 0, Core.helper.displayWidth(), (ratio)*Core.helper.displayHeight()*dividerY, 0);
                drawRect(0, Core.helper.displayHeight()-(ratio)*Core.helper.displayHeight()*(1-dividerY), Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
                for(MenuComponent component : to.components){
                    component.render(millisSinceLastTick);
                }
                to.renderForeground();
            }
        }
        @Override
        public void finalCheck(Menu from, Menu to){
            if(slideOut){
                double totalUp = 0;
                double totalDown = 0;
                for(MenuComponent c : from.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        totalDown = Math.max(totalDown, Core.helper.displayHeight()-initialY);
                    }else{
                        totalUp = Math.max(totalUp, initialY+c.height);
                    }
                }
                for(MenuComponent c : from.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        c.y = initialY+(totalDown);
                    }else{
                        c.y = initialY-(totalUp);
                    }
                }
            }else{
                double totalUp = 0;
                double totalDown = 0;
                for(MenuComponent c : to.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
                    if(y>dividerY){
                        totalDown = Math.max(totalDown, Core.helper.displayHeight()-initialY);
                    }else{
                        totalUp = Math.max(totalUp, initialY+c.height);
                    }
                }
                for(MenuComponent c : to.components){
                    double initialY = this.initialY.getOrDefault(c, c.y);
                    double y = (c.y+c.height/2)/Core.helper.displayHeight();
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
    public void onMouseMove(double x, double y){
        try{
            to.onMouseMove(x, y);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        try{
            to.onMouseButton(x, y, button, pressed, mods);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
    }
    @Override
    public boolean onMouseScrolled(double x, double y, double dx, double dy){
        try{
            return to.onMouseScrolled(x, y, dx, dy);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        return true;
    }
    @Override
    public void onCharTyped(char c){
        try{
            to.onCharTyped(c);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
    }
    @Override
    public boolean onFilesDropped(double x, double y, String[] files){
        try{
            return to.onFilesDropped(x, y, files);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        return true;
    }
    @Override
    public boolean onReturnPressed(MenuComponent component){
        try{
            return to.onReturnPressed(component);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        return true;
    }
    @Override
    public boolean onTabPressed(MenuComponent component){
        try{
            return to.onTabPressed(component);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        return true;
    }
    @Override
    public void onWindowFocused(boolean focused){
        try{
            to.onWindowFocused(focused);
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
    }
}