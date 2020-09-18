package planner.menu.configuration.overhaul.fissionsfr;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuCoolantRecipesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Coolant Recipe", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuCoolantRecipesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            CoolantRecipe b = new CoolantRecipe("New Coolant Recipe", "Input", "Output", 0, 0);
            configuration.overhaul.fissionSFR.coolantRecipes.add(b);
            Core.configuration.overhaul.fissionSFR.allCoolantRecipes.add(b);
            gui.open(new MenuCoolantRecipeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(CoolantRecipe b : configuration.overhaul.fissionSFR.coolantRecipes){
            list.add(new MenuComponentCoolantRecipeConfiguration(b));
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
            if(c instanceof MenuComponentCoolantRecipeConfiguration){
                if(button==((MenuComponentCoolantRecipeConfiguration) c).delete){
                    configuration.overhaul.fissionSFR.coolantRecipes.remove(((MenuComponentCoolantRecipeConfiguration) c).coolantRecipe);
                    Core.configuration.overhaul.fissionSFR.allCoolantRecipes.remove(((MenuComponentCoolantRecipeConfiguration) c).coolantRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentCoolantRecipeConfiguration) c).edit){
                    gui.open(new MenuCoolantRecipeConfiguration(gui, this, ((MenuComponentCoolantRecipeConfiguration) c).coolantRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}