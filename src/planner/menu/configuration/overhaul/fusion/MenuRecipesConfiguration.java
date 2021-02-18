package planner.menu.configuration.overhaul.fusion;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.Recipe;
import planner.Core;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuRecipesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add  Recipe", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuRecipesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            Recipe b = new Recipe("New  Recipe", 0, 0, 0, 0);
            configuration.overhaul.fusion.recipes.add(b);
            Core.configuration.overhaul.fusion.allRecipes.add(b);
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
        for(Recipe b : configuration.overhaul.fusion.recipes){
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
            if(c instanceof MenuComponentRecipeConfiguration){
                if(button==((MenuComponentRecipeConfiguration) c).delete){
                    configuration.overhaul.fusion.recipes.remove(((MenuComponentRecipeConfiguration) c).recipe);
                    Core.configuration.overhaul.fusion.allRecipes.remove(((MenuComponentRecipeConfiguration) c).recipe);
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