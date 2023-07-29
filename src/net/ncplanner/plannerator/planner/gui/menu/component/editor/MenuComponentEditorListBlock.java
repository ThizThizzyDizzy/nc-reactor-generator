package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentEditorListBlock extends Component implements Pinnable{
    private final MenuEdit editor;
    public final AbstractBlock block;
    public MenuComponentEditorListBlock(MenuEdit editor, AbstractBlock block){
        super(0, 0, 0, 0);
        this.editor = editor;
        this.block = block;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        Color col = isMouseFocused?Core.theme.getEditorListBackgroundMouseoverColor(Core.getThemeIndex(this)):Core.theme.getEditorListBackgroundColor(Core.getThemeIndex(this));
        renderer.setColor(col);
        renderer.fillRect(x, y, x+width, y+height);
        block.render(new Renderer(), x, y, width, height, null, null);
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
        return block.getListTooltip();
    }
    @Override
    public float getTooltipOffsetX(){
        return 0;
    }
    @Override
    public float getTooltipOffsetY(){
        return height;
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(button==0&&action==GLFW_PRESS&&editor.isShiftPressed(0)){
            Pinnable.togglePin(this);
            editor.refreshPartsList();
        }else super.onMouseButton(x, y, button, action, mods);
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        return block.getSearchableNames();
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return block.getSimpleSearchableNames();
    }
    @Override
    public String getPinnedName(){
        return block.getPinnedName();
    }
}