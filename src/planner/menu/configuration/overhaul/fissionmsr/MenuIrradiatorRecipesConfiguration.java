package planner.menu.configuration.overhaul.fissionmsr;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuIrradiatorRecipesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Irradiator Recipe", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuIrradiatorRecipesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            IrradiatorRecipe b = new IrradiatorRecipe("New Irradiator Recipe", 0, 0);
            configuration.overhaul.fissionMSR.irradiatorRecipes.add(b);
            Core.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(b);
            gui.open(new MenuIrradiatorRecipeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(IrradiatorRecipe b : configuration.overhaul.fissionMSR.irradiatorRecipes){
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
        list.width = gui.helper.displayWidth();
        list.height = gui.helper.displayHeight()-back.height-add.height;
        for(simplelibrary.opengl.gui.components.MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = gui.helper.displayWidth();
        add.height = back.height = gui.helper.displayHeight()/16;
        back.y = gui.helper.displayHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : list.components){
            if(c instanceof MenuComponentIrradiatorRecipeConfiguration){
                if(button==((MenuComponentIrradiatorRecipeConfiguration) c).delete){
                    configuration.overhaul.fissionMSR.irradiatorRecipes.remove(((MenuComponentIrradiatorRecipeConfiguration) c).irradiatorRecipe);
                    Core.configuration.overhaul.fissionMSR.allIrradiatorRecipes.remove(((MenuComponentIrradiatorRecipeConfiguration) c).irradiatorRecipe);
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