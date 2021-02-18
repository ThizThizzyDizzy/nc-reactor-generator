package planner.menu.configuration.overhaul.fusion;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe;
import planner.Core;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBreedingBlanketRecipesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Breeding Blanket Recipe", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuBreedingBlanketRecipesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            BreedingBlanketRecipe b = new BreedingBlanketRecipe("New Breeding Blanket Recipe", 0, 0);
            configuration.overhaul.fusion.breedingBlanketRecipes.add(b);
            Core.configuration.overhaul.fusion.allBreedingBlanketRecipes.add(b);
            gui.open(new MenuBreedingBlanketRecipeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(BreedingBlanketRecipe b : configuration.overhaul.fusion.breedingBlanketRecipes){
            list.add(new MenuComponentBreedingBlanketRecipeConfiguration(b));
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
            if(c instanceof MenuComponentBreedingBlanketRecipeConfiguration){
                if(button==((MenuComponentBreedingBlanketRecipeConfiguration) c).delete){
                    configuration.overhaul.fusion.breedingBlanketRecipes.remove(((MenuComponentBreedingBlanketRecipeConfiguration) c).breedingBlanketRecipe);
                    Core.configuration.overhaul.fusion.allBreedingBlanketRecipes.remove(((MenuComponentBreedingBlanketRecipeConfiguration) c).breedingBlanketRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBreedingBlanketRecipeConfiguration) c).edit){
                    gui.open(new MenuBreedingBlanketRecipeConfiguration(gui, this, ((MenuComponentBreedingBlanketRecipeConfiguration) c).breedingBlanketRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}