package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.tool.EditorTool;
import net.ncplanner.plannerator.planner.gui.Component;
public class MenuComponentEditorTool extends Component{
    public final EditorTool tool;
    public MenuComponentEditorTool(EditorTool tool){
        super(0, 0, 0, 0);
        this.tool = tool;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        Color col = isMouseFocused?Core.theme.getEditorListBackgroundMouseoverColor(Core.getThemeIndex(this)):Core.theme.getEditorListBackgroundColor(Core.getThemeIndex(this));
        renderer.setColor(col);
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getEditorToolTextColor(Core.getThemeIndex(this)));
        tool.render(renderer, x, y, width, height, Core.getThemeIndex(this));
        float border = height/8;
        if(isFocused){
            renderer.setColor(Core.theme.getEditorListLightSelectedColor(Core.getThemeIndex(this)), .85f);
            renderer.fillRect(x, y, x+border, y+border);
            renderer.fillRect(x+width-border, y, x+width, y+border);
            renderer.fillRect(x, y+height-border, x+border, y+height);
            renderer.fillRect(x+width-border, y+height-border, x+width, y+height);
            renderer.setColor(Core.theme.getEditorListDarkSelectedColor(Core.getThemeIndex(this)), .85f);
            renderer.fillRect(x+border, y, x+width-border, y+border);
            renderer.fillRect(x+border, y+height-border, x+width-border, y+height);
            renderer.fillRect(x, y+border, x+border, y+height-border);
            renderer.fillRect(x+width-border, y+border, x+width, y+height-border);
        }
        if(isMouseFocused){
            renderer.setColor(Core.theme.getEditorListLightMouseoverColor(Core.getThemeIndex(this)), .6375f);
            renderer.fillRect(x, y, x+border, y+border);
            renderer.fillRect(x+width-border, y, x+width, y+border);
            renderer.fillRect(x, y+height-border, x+border, y+height);
            renderer.fillRect(x+width-border, y+height-border, x+width, y+height);
            renderer.setColor(Core.theme.getEditorListDarkMouseoverColor(Core.getThemeIndex(this)), .6375f);
            renderer.fillRect(x+border, y, x+width-border, y+border);
            renderer.fillRect(x+border, y+height-border, x+width-border, y+height);
            renderer.fillRect(x, y+border, x+border, y+height-border);
            renderer.fillRect(x+width-border, y+border, x+width, y+height-border);
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