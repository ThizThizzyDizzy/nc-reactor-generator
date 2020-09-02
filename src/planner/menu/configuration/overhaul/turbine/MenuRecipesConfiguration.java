package planner.menu.configuration.overhaul.turbine;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Recipe;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuRecipesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Coolant Recipe", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuRecipesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            Recipe b = new Recipe("New Coolant Recipe", "Input", "Output", 0, 0);
            configuration.overhaul.turbine.recipes.add(b);
            Core.configuration.overhaul.turbine.allRecipes.add(b);
            gui.open(new MenuRecipeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Recipe b : configuration.overhaul.turbine.recipes){
            list.add(new MenuComponentRecipeConfiguration(b));
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
        list.width = Core.helper.displayWidth();
        list.height = Core.helper.displayHeight()-back.height-add.height;
        for(MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = Core.helper.displayWidth();
        add.height = back.height = Core.helper.displayHeight()/16;
        back.y = Core.helper.displayHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(MenuComponent c : list.components){
            if(c instanceof MenuComponentRecipeConfiguration){
                if(button==((MenuComponentRecipeConfiguration) c).delete){
                    configuration.overhaul.turbine.recipes.remove(((MenuComponentRecipeConfiguration) c).recipe);
                    Core.configuration.overhaul.turbine.allRecipes.remove(((MenuComponentRecipeConfiguration) c).recipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentRecipeConfiguration) c).edit){
                    gui.open(new MenuRecipeConfiguration(gui, this, ((MenuComponentRecipeConfiguration) c).recipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}