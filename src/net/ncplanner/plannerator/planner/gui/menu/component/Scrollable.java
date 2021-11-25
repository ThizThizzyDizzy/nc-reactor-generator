package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class Scrollable extends Component{
    public final float horizScrollbarHeight;
    public final float vertScrollbarWidth;
    public float scrollX = 0;
    public float scrollY = 0;
    public float maxScrollX = 0;
    public float maxScrollY = 0;
    public float horizClickOff, vertClickOff;
    public boolean horizScrollbarPresent = true;
    public boolean vertScrollbarPresent = true;
    public boolean vertPressed, horizPressed;
    public boolean vertZooming, horizZooming;
    public float scrollMagnitude;
    public float horizWidth;
    public float vertHeight;
    public float vertCenter;
    public float horizCenter;
    public double myX;
    public double myY;
    public float scrollWheelMagnitude=1;
    public Scrollable(float x, float y, float width, float height, float horizScrollbarHeight, float vertScrollbarWidth){
        super(x, y, width, height);
        this.horizScrollbarHeight = horizScrollbarHeight;
        this.vertScrollbarWidth = vertScrollbarWidth;
        scrollMagnitude = Math.min(width, height)/50;
        scrollMagnitude = scrollWheelMagnitude = 32;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        float contentHeight = 0;
        float contentWidth = 0;
        for(Component c : components){
            contentWidth = Math.max(contentWidth, c.x+c.width);
            contentHeight = Math.max(contentHeight, c.y+c.height);
        }
        maxScrollX = contentWidth-width;
        maxScrollY = contentHeight-height+(maxScrollX>0?horizScrollbarHeight:0);
        maxScrollX = contentWidth-width+(maxScrollY>0?vertScrollbarWidth:0);
        horizScrollbarPresent = maxScrollX>0;
        vertScrollbarPresent = maxScrollY>0;
        scrollX = Math.max(0, Math.min(maxScrollX, scrollX));
        scrollY = Math.max(0, Math.min(maxScrollY, scrollY));
        renderer.translate(x, y);
        drawScrollbars(renderer);
        renderer.unTranslate();
    }
    @Override
    public void render2d(double deltaTime){
        if(vertZooming){
            if(myY<vertCenter-vertHeight/2&&myY>vertScrollbarWidth) zoomUp(deltaTime);
            if(myY>vertCenter+vertHeight/2&&myY<height-vertScrollbarWidth-(horizScrollbarPresent?horizScrollbarHeight:0)) zoomDown(deltaTime);
        }
        if(horizZooming){
            if(myX<horizCenter-horizWidth/2&&myX>horizScrollbarHeight) zoomLeft(deltaTime);
            if(myX>horizCenter+horizWidth/2&&myX<width-horizScrollbarHeight-(vertScrollbarPresent?vertScrollbarWidth:0)) zoomRight(deltaTime);
        }
        Renderer renderer = new Renderer();
        renderer.bound(x, y, x+width, y+height);
        drawBackground(deltaTime);
        draw(deltaTime);
        renderer.setWhite();
        float vertWidth = vertScrollbarPresent?vertScrollbarWidth:0;
        float horizHeight = horizScrollbarPresent?horizScrollbarHeight:0;
        renderer.bound(x, y, x+width-vertWidth, y+height-horizHeight);
        renderer.translate(x-scrollX, y-scrollY);
        for(Component c : components){
            c.render2d(deltaTime);
        }
        renderer.unTranslate();
        renderer.unBound();
        drawForeground(deltaTime);
        renderer.unBound();
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(button==0&&action==GLFW_RELEASE){
            vertPressed = vertZooming = horizPressed = horizZooming = false;
        }else if(button==0&&action==GLFW_PRESS){
            double vertWidth = vertScrollbarPresent?vertScrollbarWidth:0;
            double horizHeight = horizScrollbarPresent?horizScrollbarHeight:0;
            if(horizScrollbarPresent&&y>=height-horizScrollbarHeight){
                if(MathUtil.isPointWithinRect(x, y, 0, height-horizHeight, horizHeight, height)) scrollLeft();
                else if(MathUtil.isPointWithinRect(x, y, width-vertWidth-horizHeight, height-horizHeight, width-vertWidth, height)) scrollRight();
                else if(x<horizCenter-horizWidth/2) horizZooming = true;
                else if(x>horizCenter+horizWidth/2) horizZooming = true;
                else{
                    horizClickOff = horizCenter-(float)x-horizScrollbarHeight+vertScrollbarWidth;
                    horizPressed = true;
                }
            }else if(vertScrollbarPresent&&x>=width-vertScrollbarWidth){
                if(MathUtil.isPointWithinRect(x, y, width-vertWidth, 0, width, vertWidth)) scrollUp();
                else if(MathUtil.isPointWithinRect(x, y, width-vertWidth, height-vertWidth-horizHeight, width, height-horizHeight)) scrollDown();
                else if(y<vertCenter-vertHeight/2) vertZooming = true;
                else if(y>vertCenter+vertHeight/2) vertZooming = true;
                else{
                    vertClickOff = vertCenter-(float)y-vertScrollbarWidth+horizScrollbarHeight;
                    vertPressed = true;
                }
            }
        }
        myX = (float)x;
        myY = (float)y;
        if(x>width-(vertScrollbarPresent?vertScrollbarWidth:0)||y>height-(horizScrollbarPresent?horizScrollbarHeight:0)){//Click events on the scrollbar
            x=y=Double.NaN;
        }else{
            x+=scrollX;
            y+=scrollY;
        }
        super.onMouseButton(x, y, button, action, mods);
    }
    @Override
    public void onCursorMoved(double xpos, double ypos){
        if(xpos<width-vertScrollbarWidth) vertZooming = false;
        if(ypos<height-horizScrollbarHeight) horizZooming = false;
        if(vertPressed) scrollVert(ypos-(horizScrollbarPresent?horizScrollbarHeight:0)+vertClickOff);
        if(horizPressed) scrollHoriz(xpos-(vertScrollbarPresent?vertScrollbarWidth:0)+horizClickOff);
        boolean elsewhere = false;
        if(vertScrollbarPresent&&xpos>=width-vertScrollbarWidth) elsewhere = true;
        if(horizScrollbarPresent&&ypos>=height-horizScrollbarHeight) elsewhere = true;
        myX = xpos;
        myY = ypos;
        if(!horizPressed&&!horizZooming&&!vertPressed&&!vertZooming){
            xpos+=scrollX;
            ypos+=scrollY;
            if(!elsewhere&&isMouseFocused)super.onCursorMoved(xpos, ypos);
        }
    }
    @Override
    public boolean onScroll(double dx, double dy){
        if(Core.isShiftPressed()){
            dx = dy;
            dy = 0;
        }
        if(super.onScroll(dx, dy))return true;
        boolean scrolled = false;
        if(dx>0&&scrollX>0){
            scrollX = Math.max(0, scrollX-(float)dx*scrollWheelMagnitude);//Scroll left
            scrolled = true;
        }
        if(dy>0&&scrollY>0){
            scrollY = Math.max(0, scrollY-(float)dy*scrollWheelMagnitude);//Scroll up
            scrolled = true;
        }
        if(dx<0&&scrollX<maxScrollX){
            scrollX = Math.min(maxScrollX, scrollX-(float)dx*scrollWheelMagnitude);//Scroll right
            scrolled = true;
        }
        if(dy<0&&scrollY<maxScrollY){
            scrollY = Math.min(maxScrollY, scrollY-(float)dy*scrollWheelMagnitude);//Scroll down
            scrolled = true;
        }
        return scrolled;
    }
    private void drawScrollbars(Renderer renderer) {
        if(vertScrollbarPresent){
            float barTop = 0;
            float spaceHeight = height-(horizScrollbarPresent?horizScrollbarHeight:0);
            float barBottom = spaceHeight;
            float barLeft = width-vertScrollbarWidth;
            drawVerticalScrollbarBackground(renderer, barLeft, 0, vertScrollbarWidth, barBottom);
            drawUpwardScrollbarButton(renderer, barLeft, barTop, vertScrollbarWidth, vertScrollbarWidth);
            barTop+=vertScrollbarWidth;
            drawDownwardScrollbarButton(renderer, barLeft, barBottom-vertScrollbarWidth, vertScrollbarWidth, vertScrollbarWidth);
            barBottom-=vertScrollbarWidth;
            float percentY = maxScrollY<=1?0.99f:spaceHeight/(maxScrollY+spaceHeight);
            float barSpace = barBottom-barTop;
            float barHeight = percentY*barSpace;
            float barShift = barSpace-barHeight;
            percentY = scrollY/(maxScrollY>0?maxScrollY:1);
            float posY = percentY*barShift;
            vertHeight = barHeight;
            vertCenter = barTop+posY+barHeight/2;
            drawVerticalScrollbarForeground(renderer, barLeft, barTop+posY, vertScrollbarWidth, barHeight);
        }
        if(horizScrollbarPresent){
            float barLeft = 0;
            float spaceWidth = width-(vertScrollbarPresent?vertScrollbarWidth:0);
            float barRight = spaceWidth;
            float barTop = height-horizScrollbarHeight;
            drawHorizontalScrollbarBackground(renderer, 0, barTop, barRight, horizScrollbarHeight);
            drawLeftwardScrollbarButton(renderer, barLeft, barTop, horizScrollbarHeight, horizScrollbarHeight);
            barLeft+=horizScrollbarHeight;
            drawRightwardScrollbarButton(renderer, barRight-horizScrollbarHeight, barTop, horizScrollbarHeight, horizScrollbarHeight);
            barRight-=horizScrollbarHeight;
            float percentX = maxScrollX<=1?0.99f:spaceWidth/(maxScrollX+spaceWidth);
            float barSpace = barRight-barLeft;
            float barWidth = percentX*barSpace;
            float barShift = barSpace-barWidth;
            percentX = scrollX/(maxScrollX>0?maxScrollX:1);
            float posX = percentX*barShift;
            horizWidth = barWidth;
            horizCenter = barLeft+posX+barWidth/2;
            drawHorizontalScrollbarForeground(renderer, barLeft+posX, barTop, barWidth, horizScrollbarHeight);
        }
    }
    public void drawUpwardScrollbarButton(Renderer renderer, float x, float y, float width, float height){
        drawButton(renderer, x, y, width, height);
        renderer.fillTri(x+width/2, y+height/4, x+width/4, y+3*height/4, x+3*width/4, y+3*height/4);
    }
    public void drawDownwardScrollbarButton(Renderer renderer, float x, float y, float width, float height){
        drawButton(renderer, x, y, width, height);
        renderer.fillTri(x+width/4, y+height/4, x+3*width/4, y+height/4, x+width/2, y+3*height/4);
    }
    public void drawRightwardScrollbarButton(Renderer renderer, float x, float y, float width, float height){
        drawButton(renderer, x, y, width, height);
        renderer.fillTri(x+width/4, y+height/4, x+width/4, y+3*height/4, x+3*width/4, y+height/2);
    }
    public void drawLeftwardScrollbarButton(Renderer renderer, float x, float y, float width, float height){
        drawButton(renderer, x, y, width, height);
        renderer.fillTri(x+width/4, y+height/2, x+3*width/4, y+height/4, x+3*width/4, y+3*height/4);
    }
    public void drawVerticalScrollbarBackground(Renderer renderer, float x, float y, float width, float height){
        renderer.setColor(Core.theme.getScrollbarBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
    }
    public void drawVerticalScrollbarForeground(Renderer renderer, float x, float y, float width, float height){
        renderer.setColor(Core.theme.getScrollbarButtonColor());
        renderer.fillRect(x, y, x+width, y+height);
    }
    public void drawHorizontalScrollbarBackground(Renderer renderer, float x, float y, float width, float height){
        renderer.setColor(Core.theme.getScrollbarBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
    }
    public void drawHorizontalScrollbarForeground(Renderer renderer, float x, float y, float width, float height){
        renderer.setColor(Core.theme.getScrollbarButtonColor());
        renderer.fillRect(x, y, x+width, y+height);
    }
    public void drawButton(Renderer renderer, float x, float y, float width, float height){
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getScrollbarButtonColor());
        renderer.fillRect(x+1, y+1, x+width-1, y+height-1);
    }
    public void scrollUp(){
        scrollY = Math.max(0, scrollY-scrollMagnitude);
    }
    public void scrollDown(){
        scrollY = Math.min(maxScrollY, scrollY+scrollMagnitude);
    }
    public void scrollLeft(){
        scrollX = Math.max(0, scrollX-scrollMagnitude);
    }
    public void scrollRight(){
        scrollX = Math.min(maxScrollX, scrollX+scrollMagnitude);
    }
    public void scrollVert(double y){
        if(maxScrollY<1) return;
        double spaceHeight = height-(horizScrollbarPresent?horizScrollbarHeight:0);
        double percentY = maxScrollY<=1?2:spaceHeight/(maxScrollY+spaceHeight);
        double barHeight = (percentY*(spaceHeight-vertScrollbarWidth*2));
        y-=barHeight/2;
        double maxEffective = Math.round(spaceHeight-vertScrollbarWidth*2-barHeight);
        if(maxEffective<=0) return;//Should never be the case, but we can't be certain; this will prevent any possible /0 errors
        if(y<0) scrollY = 0;
        else if(y>maxEffective) scrollY = maxScrollY;
        else scrollY = (Math.round(y/maxEffective*maxScrollY));
    }
    public void scrollHoriz(double x){
        if(maxScrollX<1) return;
        double spaceWidth = width-(vertScrollbarPresent?vertScrollbarWidth:0);
        double percentX = maxScrollX<1?2:spaceWidth/(maxScrollX+spaceWidth);
        double barWidth = (percentX*(spaceWidth-horizScrollbarHeight*2));
        x-=barWidth/2;
        double maxEffective = Math.round(spaceWidth-horizScrollbarHeight*2-barWidth);
        if(maxEffective<=0) return;
        if(x<0) scrollX = 0;
        else if(x>maxEffective) scrollX = maxScrollX;
        else scrollX = (Math.round(x/maxEffective*maxScrollX));
    }
    public boolean hasVertScrollbar(){
        return vertScrollbarPresent&&vertScrollbarWidth>0;
    }
    public boolean hasHorizScrollbar(){
        return horizScrollbarPresent&&horizScrollbarHeight>0;
    }
    public double getVertScroll(){ return scrollY; }
    public double getHorizScroll(){ return scrollX; }
    private void zoomUp(double deltaTime){
        scrollY = (float)Math.max(0, scrollY-(height-(horizScrollbarPresent?horizScrollbarHeight:0))/2*(deltaTime*20));
    }
    private void zoomDown(double deltaTime){
        scrollY = (float)Math.min(maxScrollY, scrollY+(height-(horizScrollbarPresent?horizScrollbarHeight:0))/2*(deltaTime*20));
    }
    private void zoomLeft(double deltaTime){
        scrollX = (float)Math.min(0, scrollX-(width-(vertScrollbarPresent?vertScrollbarWidth:0))/2*(deltaTime*20));
    }
    private void zoomRight(double deltaTime){
        scrollX = (float)Math.max(maxScrollX, scrollX+(width-(vertScrollbarPresent?vertScrollbarWidth:0))/2*(deltaTime*20));
    }
}