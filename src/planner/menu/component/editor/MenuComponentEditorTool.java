package planner.menu.component.editor;
import planner.core.Color;
import planner.Core;
import planner.editor.tool.EditorTool;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorTool extends MenuComponent{
    public final EditorTool tool;
    public MenuComponentEditorTool(EditorTool tool){
        super(0, 0, 0, 0);
        this.tool = tool;
    }
    @Override
    public void render(){
        Color col = isMouseOver?Core.theme.getBrighterEditorListBorderColor():Core.theme.getEditorListBorderColor();
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
        double border = height/8;
        if(isSelected){
            Core.applyColor(Core.theme.getDarkerEditorListBorderColor(), .85f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            Core.applyColor(Core.theme.getDarkerTextColor(), .85f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
        if(isMouseOver){
            Core.applyColor(Core.theme.getEditorListBorderColor(), .6375f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            Core.applyColor(Core.theme.getTextColor(), .6375f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
    }
    public void drawText(){
        tool.render(x, y, width, height);
    }
    @Override
    public String getTooltip(){
        return tool.getTooltip();
    }
    @Override
    public double getTooltipOffsetX(){
        return 0;
    }
    @Override
    public double getTooltipOffsetY(){
        return height;
    }
}