package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.turbine;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentRecipe extends Component{
    public final Recipe recipe;
    public final Button edit = add(new Button("", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify recipe"));
    public final Button delete = add(new Button("", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("delete", x, y, width, height);
        }
    }.setTooltip("Delete recipe"));
    public MenuComponentRecipe(Recipe recipe, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.recipe = recipe;
        edit.addAction(onEditPressed);
        delete.addAction(onDeletePressed);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        delete.x = width-height/2-height/4;
        edit.x = delete.x - height;
        delete.y = edit.y = height/4;
        delete.width = delete.height = edit.width = edit.height = height/2;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        renderer.fillRect(x, y, x+width, y+height);
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(recipe.inputTexture!=null)renderer.drawImage(recipe.inputDisplayTexture, x, y, x+height, y+height);
        if(recipe.outputTexture!=null)renderer.drawImage(recipe.outputDisplayTexture, x+height, y, x+height*2, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        renderer.drawText(x+height*2, y, x+width, y+height/4, recipe.getInputDisplayName());
        renderer.drawText(x+height*2, y+height/4, x+width, y+height/4*2, recipe.getOutputDisplayName());
        renderer.drawText(x+height*2, y+height/4*2, x+width, y+height/4*3, "Power: "+recipe.power);
        renderer.drawText(x+height*2, y+height/4*3, x+width, y+height/4*4, "Expansion Coefficient: "+recipe.coefficient);
    }
}
