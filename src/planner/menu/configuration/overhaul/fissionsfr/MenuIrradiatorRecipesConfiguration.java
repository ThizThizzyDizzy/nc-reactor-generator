package planner.menu.configuration.overhaul.fissionsfr;
import org.lwjgl.opengl.Display;
import planner.Core;
import multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuIrradiatorRecipesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Irradiator Recipe", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    public MenuIrradiatorRecipesConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        add.addActionListener((e) -> {
            IrradiatorRecipe b = new IrradiatorRecipe("New Irradiator Recipe", 0, 0);
            Core.configuration.overhaul.fissionSFR.irradiatorRecipes.add(b);
            gui.open(new MenuIrradiatorRecipeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(IrradiatorRecipe b : Core.configuration.overhaul.fissionSFR.irradiatorRecipes){
            list.add(new MenuComponentIrradiatorRecipeConfiguration(b));
        }
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        list.width = Display.getWidth();
        list.height = Display.getHeight()-back.height-add.height;
        for(MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = Display.getWidth();
        add.height = back.height = Display.getHeight()/16;
        back.y = Display.getHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(MenuComponent c : list.components){
            if(c instanceof MenuComponentIrradiatorRecipeConfiguration){
                if(button==((MenuComponentIrradiatorRecipeConfiguration) c).delete){
                    Core.configuration.overhaul.fissionSFR.irradiatorRecipes.remove(((MenuComponentIrradiatorRecipeConfiguration) c).irradiatorRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentIrradiatorRecipeConfiguration) c).edit){
                    gui.open(new MenuIrradiatorRecipeConfiguration(gui, this, ((MenuComponentIrradiatorRecipeConfiguration) c).irradiatorRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}