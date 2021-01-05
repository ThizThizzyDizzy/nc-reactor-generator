package planner.menu.component.editor;
import planner.Core;
import planner.menu.MenuEdit;
import planner.editor.suggestion.Suggestion;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentSuggestion extends MenuComponent{
    private final MenuEdit editor;
    public final Suggestion suggestion;
    public boolean enabled = false;
    public MenuComponentSuggestion(MenuEdit editor, Suggestion suggestion){
        super(0, 0, 0, 64);
        this.editor = editor;
        this.suggestion = suggestion;
    }
    @Override
    public void render(){
        if(isMouseOver&&!enabled)Core.applyAverageColor(Core.theme.getButtonColor(), Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(enabled?Core.theme.getSelectedMultiblockColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText(suggestion.getName());
    }
    public void drawText(String text){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public String getTooltip(){
        return suggestion.getDescription();
    }
    @Override
    public void onMouseMove(double x, double y){
        super.onMouseMove(x, y);
        suggestion.selected = true;
    }
    @Override
    public void onMouseMovedElsewhere(double x, double y){
        super.onMouseMovedElsewhere(x, y);
        suggestion.selected = false;
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(pressed){
//            if(button==1){
//                editor.suggestions.remove(suggestion);
//            }
            if(button==0){
                suggestion.apply(editor.multiblock);
            }
        }
    }
}