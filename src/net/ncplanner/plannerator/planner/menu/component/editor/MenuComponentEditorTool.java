package net.ncplanner.plannerator.planner.menu.component.editor;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.tool.EditorTool;
import simplelibrary.image.Color;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorTool extends MenuComponent{
    public final EditorTool tool;
    public MenuComponentEditorTool(EditorTool tool){
        super(0, 0, 0, 0);
        this.tool = tool;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        Color col = isMouseOver?Core.theme.getEditorListBackgroundMouseoverColor(Core.getThemeIndex(this)):Core.theme.getEditorListBackgroundColor(Core.getThemeIndex(this));
        renderer.setColor(col);
        drawRect(x, y, x+width, y+height, 0);
        renderer.setColor(Core.theme.getEditorToolTextColor(Core.getThemeIndex(this)));
        tool.render(new Renderer(), x, y, width, height, Core.getThemeIndex(this));
        double border = height/8;
        if(isSelected){
            renderer.setColor(Core.theme.getEditorListLightSelectedColor(Core.getThemeIndex(this)), .85f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            renderer.setColor(Core.theme.getEditorListDarkSelectedColor(Core.getThemeIndex(this)), .85f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
        if(isMouseOver){
            renderer.setColor(Core.theme.getEditorListLightMouseoverColor(Core.getThemeIndex(this)), .6375f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            renderer.setColor(Core.theme.getEditorListDarkMouseoverColor(Core.getThemeIndex(this)), .6375f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
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