package planner.menu.configuration.overhaul.fissionmsr;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionmsr.Block;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import org.lwjgl.glfw.GLFW;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuAddonBlockConfiguration extends ConfigurationMenu{
    private final Block block;
    private final MenuComponentLabel blockRecipesLabel;
    private final MenuComponentMinimaList blockRecipes;
    private final MenuComponentMinimalistButton addBlockRecipe;
    private boolean refreshNeeded = false;
    private final Block parentBlock;
    public MenuAddonBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block parentBlock, Block block){
        super(gui, parent, configuration, parentBlock.getDisplayName());
        blockRecipesLabel = add(new MenuComponentLabel(sidebar.width, 0, 0, 48, "Block Recipes", true));
        blockRecipes = add(new MenuComponentMinimaList(sidebar.width, blockRecipesLabel.y+blockRecipesLabel.height, 0, 0, 16));
        addBlockRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "New Recipe", true, true));
        addBlockRecipe.addActionListener((e) -> {
            BlockRecipe recipe = new BlockRecipe("nuclearcraft:input", "nuclearcraft:output");
            parentBlock.allRecipes.add(recipe);
            block.recipes.add(recipe);
            onGUIClosed();
            gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, parentBlock, recipe));
        });
        this.parentBlock = parentBlock;
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        if(block.recipes.size()>0)blockRecipesLabel.text = "Block Recipes ("+block.recipes.size()+")";
        blockRecipes.components.clear();
        for(BlockRecipe recipe : block.recipes){
            blockRecipes.add(new MenuComponentBlockRecipe(block, recipe));
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
        blockRecipesLabel.width = blockRecipes.width = addBlockRecipe.width = gui.helper.displayWidth()-sidebar.width;
        addBlockRecipe.y = gui.helper.displayHeight()-addBlockRecipe.height;
        blockRecipes.height = addBlockRecipe.y-blockRecipes.y;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : blockRecipes.components){
            if(c instanceof MenuComponentBlockRecipe){
                if(button==((MenuComponentBlockRecipe) c).delete){
                    parentBlock.allRecipes.remove(((MenuComponentBlockRecipe)c).blockRecipe);
                    block.recipes.remove(((MenuComponentBlockRecipe)c).blockRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlockRecipe) c).edit){
                    onGUIClosed();
                    gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, parentBlock, ((MenuComponentBlockRecipe) c).blockRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_MIDDLE&&pressed){
            for(BlockRecipe recipe : block.recipes){
                recipe.inputDisplayName = recipe.inputDisplayName.replace("Molten FLiBe Salt Solution of ", "").replace(" Fuel", "");
                recipe.outputDisplayName = recipe.outputDisplayName.replace("Molten FLiBe Salt Solution of ", "").replace(" Fuel", "");
            }
        }
    }
}