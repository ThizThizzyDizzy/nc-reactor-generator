package planner.menu.component;
import java.awt.Color;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorListBlock extends MenuComponent{
    private final MenuEdit editor;
    public final Block block;
    public MenuComponentEditorListBlock(MenuEdit editor, Block block){
        super(0, 0, 0, 0);
        this.editor = editor;
        this.block = block;
    }
    @Override
    public void render(){
        Color col = Core.theme.getEditorListBorderColor();
        if(isMouseOver)col = col.brighter();
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
        double border = height/8;
        if(isSelected){
            Core.applyColor(Core.theme.getEditorListBorderColor().darker(), .85f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            Core.applyColor(Core.theme.getTextColor().darker(), .85f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
        if(isMouseOver){
            editor.setTooltip(block.getListTooltip());
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
        block.render(x, y, width, height, false);
    }
}