package planner.menu;
import java.util.ArrayList;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentScrollable;
public class Menu extends simplelibrary.opengl.gui.Menu{
    public int tooltipTime = 15;
    public int tooltipTimer = 0;
    public boolean tooltipShowing = false;
    private boolean mouseMoving = false;
    public Menu(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void renderForeground(){
        if(tooltipShowing){
            for(MenuComponent c : getComponents()){
                if(c instanceof MenuComponentTooltip){
                    MenuComponentTooltip co = (MenuComponentTooltip)c;
                    if(c.isMouseOver&&co.getTooltip()!=null){
                        renderTooltip(c);
                        break;
                    }
                }
            }
        }
    }
    @Override
    public void tick(){
        super.tick();
        if(tooltipShowing){
            boolean hasMouseover = false;
            for(MenuComponent c : getComponents()){
                if(c instanceof MenuComponentTooltip){
                    MenuComponentTooltip co = (MenuComponentTooltip)c;
                    if(c.isMouseOver&&co.getTooltip()!=null){
                        hasMouseover = true;
                        break;
                    }
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
                for(MenuComponent c : getComponents()){
                    if(c instanceof MenuComponentTooltip){
                        MenuComponentTooltip co = (MenuComponentTooltip)c;
                        if(c.isMouseOver&&co.getTooltip()!=null){
                            hasMouseover = true;
                            break;
                        }
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
    @Override
    public void onMouseMove(double x, double y){
        super.onMouseMove(x, y);
        mouseMoving = true;
    }
    private void renderTooltip(MenuComponent component){
        MenuComponentTooltip tc = (MenuComponentTooltip)component;
        String tooltip = tc.getTooltip();
        if(tooltip==null)return;
        FontManager.setFont("font");
        double textHeight = 20;
        double textSpacing = textHeight/10;
        double borderSpacing = textHeight/4;
        double borderWidth = textHeight/10;
        String[] tooltips = tooltip.split("\\\n");
        double textWidth = 0;
        for(String s : tooltips){
            textWidth = Math.max(textWidth, FontManager.getLengthForStringWithHeight(s, textHeight));
        }
        double tooltipWidth = borderWidth+borderSpacing+textWidth+borderSpacing+borderWidth;
        double tooltipHeight = borderWidth+borderSpacing+textHeight*tooltips.length+textSpacing*(tooltips.length-1)+borderSpacing+borderWidth;
        double tooltipX = component.x+tc.getTooltipOffsetX();
        double tooltipY = component.y+tc.getTooltipOffsetY();
        MenuComponent comp = component;
        while(comp.parent!=null&&comp.parent instanceof MenuComponent){
            comp = (MenuComponent)comp.parent;
            if(comp instanceof MenuComponentScrollable){
                tooltipX-=((MenuComponentScrollable)comp).getHorizScroll();
                tooltipY-=((MenuComponentScrollable)comp).getVertScroll();
            }
            tooltipX+=comp.x;
            tooltipY+=comp.y;
        }
        tooltipX = Math.min(tooltipX, Core.helper.displayWidth()-tooltipWidth);
        tooltipY = Math.min(tooltipY, Core.helper.displayHeight()-tooltipHeight);
        if(tooltipWidth>Core.helper.displayWidth())tooltipX = 0;
        if(tooltipHeight>Core.helper.displayHeight())tooltipY = 0;
        Core.applyColor(Core.theme.getTextColor());
        drawRect(tooltipX, tooltipY, tooltipX+tooltipWidth, tooltipY+tooltipHeight, 0);
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(tooltipX+borderWidth, tooltipY+borderWidth, tooltipX+tooltipWidth-borderWidth, tooltipY+tooltipHeight-borderWidth, 0);
        Core.applyColor(Core.theme.getTextColor());
        for(int i = 0; i<tooltips.length; i++){
            String tt = tooltips[i];
            drawText(tooltipX+borderWidth+borderSpacing, tooltipY+borderWidth+borderSpacing+(textHeight+textSpacing)*i, tooltipX+tooltipWidth, tooltipY+borderWidth+borderSpacing+(textHeight+textSpacing)*i+textHeight, tt);
        }
        FontManager.setFont("high resolution");
    }
    private Iterable<MenuComponent> getComponents(){
        ArrayList<MenuComponent> comps = new ArrayList<>(components);
        for(MenuComponent c : components){
            comps.addAll(c.components);
        }
        return comps;
    }
}