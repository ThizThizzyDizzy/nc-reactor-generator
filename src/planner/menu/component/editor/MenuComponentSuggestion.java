package planner.menu.component.editor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import planner.Core;
import planner.editor.suggestion.Suggestion;
import planner.menu.MenuEdit;
import planner.menu.component.Searchable;
import simplelibrary.font.FontManager;
import simplelibrary.image.Image;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentSuggestion extends MenuComponent implements Searchable{
    private final MenuEdit editor;
    public final Suggestion suggestion;
    public boolean enabled = false;
    public MenuComponentSuggestion(MenuEdit editor, Suggestion suggestion){
        super(0, 0, 0, 32);
        this.editor = editor;
        this.suggestion = suggestion;
    }
    @Override
    public void render(){
        if(isSelected){
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        int i = 0;
        Core.applyWhite();
        for(Image image : suggestion.getImages()){
            drawRect(x+height*i, y, x+height*(i+1), y+height, Core.getTexture(image));
            i++;
        }
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(suggestion.getName());
    }
    public void drawText(String text){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-height*suggestion.getImages().length)/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x+height*suggestion.getImages().length, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
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
    @Override
    public List<String> getSearchableNames(){
        ArrayList<String> lst = new ArrayList<>(Arrays.asList(suggestion.getName()));
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
}