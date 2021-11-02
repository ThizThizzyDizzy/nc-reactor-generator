package net.ncplanner.plannerator.planner.menu.component.generator;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMultiblockGenerator extends MenuComponent{
    private final MultiblockGenerator generator;
    public MenuComponentMultiblockGenerator(MultiblockGenerator generator){
        super(0, 0, 0, 96);
        this.generator = generator;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        if(isSelected){
            if(isMouseOver)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        double height = this.height/2;
        double textLength = FontManager.getLengthForStringWithHeight(generator.getName(), height)+height;
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+this.height/2-textHeight/2, x+width, y+this.height/2+textHeight/2, generator.getName());
    }
}