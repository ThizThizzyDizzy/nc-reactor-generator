package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentRecipe extends Component{
    public final Recipe recipe;
    public final Button edit = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillTri(x+width*.25f, y+height*.75f,
                    x+width*.375f, y+height*.75f,
                    x+width*.25f, y+height*.625f);
            renderer.fillQuad(x+width*.4f, y+height*.725f,
                    x+width*.275f, y+height*.6f,
                    x+width*.5f, y+height*.375f,
                    x+width*.625f, y+height*.5f);
            renderer.fillQuad(x+width*.525f, y+height*.35f,
                    x+width*.65f, y+height*.475f,
                    x+width*.75f, y+height*.375f,
                    x+width*.625f, y+height*.25f);
        }
    }.setTooltip("Modify recipe"));
    public final Button delete = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillQuad(x+width*.1f, y+height*.8f,
                    x+width*.2f, y+height*.9f,
                    x+width*.9f, y+height*.2f,
                    x+width*.8f, y+height*.1f);
            renderer.fillQuad(x+width*.1f, y+height*.2f,
                    x+width*.2f, y+height*.1f,
                    x+width*.9f, y+height*.8f,
                    x+width*.8f, y+height*.9f);
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
        renderer.drawText(x+height*2, y, x+width, y+height/6, recipe.getInputDisplayName());
        renderer.drawText(x+height*2, y+height/6, x+width, y+height/6*2, recipe.getOutputDisplayName());
        renderer.drawText(x+height*2, y+height/6*2, x+width, y+height/6*3, "Efficiency: "+recipe.efficiency);
        renderer.drawText(x+height*2, y+height/6*3, x+width, y+height/6*4, "Heat: "+recipe.heat);
        renderer.drawText(x+height*2, y+height/6*4, x+width, y+height/6*5, "Time: "+recipe.time);
        renderer.drawText(x+height*2, y+height/6*5, x+width, y+height/6*6, "Fluxiness: "+recipe.fluxiness);
    }
}
