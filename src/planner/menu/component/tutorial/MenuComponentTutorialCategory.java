package planner.menu.component.tutorial;
import planner.Core;
import planner.editor.tutorial.TutorialCategory;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTutorialCategory extends MenuComponent{
    private final TutorialCategory category;
    public MenuComponentTutorialCategory(TutorialCategory category){
        super(0, 0, 0, 0);
        this.category = category;
    }
    @Override
    public void render(){
        if(isMouseOver&&!isSelected)Core.applyAverageColor(Core.theme.getButtonColor(), Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(isSelected?Core.theme.getSelectedMultiblockColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(category.name, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, category.name);
    }
}