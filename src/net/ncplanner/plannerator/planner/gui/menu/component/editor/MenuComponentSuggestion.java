package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import java.util.Arrays;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Searchable;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentSuggestion extends Component implements Searchable{
    private final MenuEdit editor;
    public final Suggestion suggestion;
    public boolean enabled = false;
    public MenuComponentSuggestion(MenuEdit editor, Suggestion suggestion){
        super(0, 0, 0, 32);
        this.editor = editor;
        this.suggestion = suggestion;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(isFocused){
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        renderer.fillRect(x, y, x+width, y+height);
        int i = 0;
        renderer.setWhite();
        for(Image image : suggestion.getImages()){
            renderer.drawImage(image, x+height*i, y, x+height*(i+1), y+height);
            i++;
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer, suggestion.getName());
    }
    public void drawText(Renderer renderer, String text){
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-height*suggestion.getImages().length)/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(x+height*suggestion.getImages().length, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public String getTooltip(){
        return suggestion.getDescription();
    }
    @Override
    public void onCursorMoved(double xpos, double ypos){
        suggestion.selected = isMouseFocused;
        super.onCursorMoved(xpos, ypos);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(action==GLFW_PRESS){
//            if(button==1){
//                editor.suggestions.remove(suggestion);
//            }
            if(button==0){
                suggestion.apply(editor.multiblock);
            }
        }
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = getSimpleSearchableNames();
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> lst = new ArrayList<>(Arrays.asList(suggestion.getName()));
        return lst;
    }
}