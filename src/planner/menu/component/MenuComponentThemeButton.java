package planner.menu.component;
import planner.Core;
import planner.theme.Theme;
import simplelibrary.font.FontManager;
public class MenuComponentThemeButton extends MenuComponentMinimalistButton{
    private final Theme theme;
    public MenuComponentThemeButton(Theme theme){
        super(0, 0, 0, 48, theme.name, true, true);
        this.theme = theme;
        addActionListener((e) -> {
            Core.setTheme(theme);
        });
    }
    @Override
    public void render(){
        theme.drawThemeButtonBackground(x, y, width, height, darker, enabled, isPressed, isMouseOver);
        double textLength = FontManager.getLengthForStringWithHeight(label, height);
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = (int)((height-textInset*2)*scale)-4;
        theme.drawThemeButtonText(x, y, width, height, textHeight, label);
    }
}