package net.ncplanner.plannerator.planner.gui;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Scrollable;
public class Menu extends Component{
    public Menu parent;
    public int tooltipTime = 15;
    public int tooltipTimer = 0;
    public boolean tooltipShowing = false;
    private boolean mouseMoving = false;
    public Menu(GUI gui, Menu parent){
        this.gui = gui;
        this.parent = parent;
    }
    @Override
    public void render2d(double deltaTime){
        super.render2d(deltaTime);
        if(tooltipShowing){
            boolean hasMouseover = false;
            for(Component c : getAllComponents()){
                if(c.isMouseFocused&&c.getTooltip()!=null){
                    hasMouseover = true;
                    break;
                }
            }
            if(hasMouseover)tooltipTimer--;
            else tooltipTimer++;
            if(tooltipTimer>=tooltipTime){
                tooltipShowing = false;
                tooltipTimer = 0;
            }
        }else{
            if(!mouseMoving){
                boolean hasMouseover = false;
                for(Component c : getAllComponents()){
                    if(c.isMouseFocused&&c.getTooltip()!=null){
                        hasMouseover = true;
                        break;
                    }
                }
                if(hasMouseover)tooltipTimer++;
                else tooltipTimer--;
            }else tooltipTimer--;
            if(tooltipTimer>=tooltipTime){
                tooltipShowing = true;
                tooltipTimer = 0;
            }
        }
        tooltipTimer = Math.min(tooltipTime, Math.max(0, tooltipTimer));
        mouseMoving = false;
    }
    public void render3d(double deltaTime){}
    @Override
    public void drawForeground(double deltaTime){
        if(tooltipShowing){
            for(Component c : getAllComponents()){
                if(c.isMouseFocused&&c.getTooltip()!=null){
                    renderTooltip(c);
                    break;
                }
            }
        }
    }
    public void renderTooltip(Component component){
        Renderer renderer = new Renderer();
        float textHeight = 20;
        float textSpacing = textHeight/10;
        float borderSpacing = textHeight/4;
        float borderWidth = textHeight/10;
        String[] tooltips = component.getTooltip().split("\\\n");
        float textWidth = 0;
        for(String s : tooltips){
            textWidth = Math.max(textWidth, renderer.getStringWidth(s, textHeight));
        }
        float tooltipWidth = borderWidth+borderSpacing+textWidth+borderSpacing+borderWidth;
        float tooltipHeight = borderWidth+borderSpacing+textHeight*tooltips.length+textSpacing*(tooltips.length-1)+borderSpacing+borderWidth;
        float tooltipX = component.x+component.getTooltipOffsetX();
        float tooltipY = component.y+component.getTooltipOffsetY();
        Component comp = component;
        while(comp.parent!=null&&comp.parent instanceof Component){
            comp = (Component)comp.parent;
            if(comp instanceof Scrollable){
                tooltipX-=((Scrollable)comp).getHorizScroll();
                tooltipY-=((Scrollable)comp).getVertScroll();
            }
            tooltipX+=comp.x;
            tooltipY+=comp.y;
        }
        tooltipX = Math.min(tooltipX, gui.getWidth()-tooltipWidth);
        tooltipY = Math.min(tooltipY, gui.getHeight()-tooltipHeight);
        if(tooltipWidth>gui.getWidth())tooltipX = 0;
        if(tooltipHeight>gui.getHeight())tooltipY = 0;
        renderer.setColor(Core.theme.getTooltipBorderColor());
        renderer.fillRect(tooltipX, tooltipY, tooltipX+tooltipWidth, tooltipY+tooltipHeight);
        renderer.setColor(Core.theme.getTooltipBackgroundColor());
        renderer.fillRect(tooltipX+borderWidth, tooltipY+borderWidth, tooltipX+tooltipWidth-borderWidth, tooltipY+tooltipHeight-borderWidth);
        renderer.setColor(Core.theme.getTooltipTextColor());
        for(int i = 0; i<tooltips.length; i++){
            String tt = tooltips[i];
            renderer.drawText(tooltipX+borderWidth+borderSpacing, tooltipY+borderWidth+borderSpacing+(textHeight+textSpacing)*i, tooltipX+tooltipWidth, tooltipY+borderWidth+borderSpacing+(textHeight+textSpacing)*i+textHeight, tt);
        }
    }
    public void onClosed(){}
    public void onOpened(){}
    @Override
    public void onCursorMoved(double xpos, double ypos){
        super.onCursorMoved(xpos, ypos);
        mouseMoving = true;
    }
}