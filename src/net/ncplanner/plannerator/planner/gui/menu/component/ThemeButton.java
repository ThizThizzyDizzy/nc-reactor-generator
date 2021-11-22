package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.theme.Theme;
public class ThemeButton extends Button{
    private final Theme theme;
    public ThemeButton(Theme theme){
        super(0, 0, 0, 48, theme.name, true, true);
        this.theme = theme;
        addAction(() -> {
            Core.setTheme(theme);
        });
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        theme.drawThemeButtonBackground(x, y, width, height, darker, enabled, pressed, isMouseFocused);
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-textInset*2)/textLength);
        float textHeight = (int)((height-textInset*2)*scale)-4;
        theme.drawThemeButtonText(x, y, width, height, textHeight, text);
    }
}