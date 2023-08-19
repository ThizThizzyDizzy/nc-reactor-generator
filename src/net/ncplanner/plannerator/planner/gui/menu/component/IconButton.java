package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
public class IconButton extends Button{
    private final String icon;
    public IconButton(String icon, boolean enabled){
        super("", enabled);
        this.icon = icon;
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        renderer.drawElement(icon, x, y, width, height);
    }
}