package planner.menu.configuration;
import multiblock.configuration.Configuration;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentAddon extends MenuComponent{
    public final Configuration addon;
    double textInset = 4;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2d(x+width*.25, y+height*.75);
            GL11.glVertex2d(x+width*.375, y+height*.75);
            GL11.glVertex2d(x+width*.25, y+height*.625);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d(x+width*.4, y+height*.725);
            GL11.glVertex2d(x+width*.275, y+height*.6);
            GL11.glVertex2d(x+width*.5, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.5);

            GL11.glVertex2d(x+width*.525, y+height*.35);
            GL11.glVertex2d(x+width*.65, y+height*.475);
            GL11.glVertex2d(x+width*.75, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.25);
            GL11.glEnd();
        }
    }.setTooltip("Modify addon"));
    public final MenuComponentMinimalistButton remove;
    public MenuComponentAddon(Configuration addon){
        super(0, 0, 0, 48);
        this.addon = addon;
        remove = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Remove", true, true, true).setTooltip("Remove or Delete Addon"));
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        edit.x = width-height*3/2;
        remove.width = edit.width*3;
        remove.x = edit.x-remove.width-height;
        remove.y = edit.y = 0;
        remove.height = edit.width = edit.height = height;
    }
    @Override
    public void render(){
        if(isMouseOver)Core.applyColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        String str;
        if(addon.overhaulVersion==null&&addon.underhaulVersion==null){
            str = addon.name;
        }else if(addon.overhaulVersion!=null&&addon.underhaulVersion!=null){
            str = addon.name+" "+addon.overhaulVersion+" | "+addon.underhaulVersion;
        }else{
            str = addon.name+" "+(addon.overhaulVersion==null?addon.underhaulVersion:addon.overhaulVersion);
        }
        drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, str);
    }
}