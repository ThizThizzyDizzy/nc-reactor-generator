package planner.menu.configuration.overhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.Block;
import multiblock.configuration.overhaul.turbine.Recipe;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.overhaul.turbine.MenuBlockConfiguration;
import planner.menu.configuration.overhaul.turbine.MenuComponentBlock;
import planner.menu.configuration.overhaul.turbine.MenuComponentRecipe;
import planner.menu.configuration.overhaul.turbine.MenuRecipeConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuOverhaulTurbineConfiguration extends ConfigurationMenu{
    private final MenuComponentMinimalistTextBox minWidth, minLength, maxSize, fluidPerBlade, throughputEfficiencyLeniencyMult, throughputEfficiencyLeniencyThreshold, throughputFactor, powerBonus;
    private final MenuComponentLabel blocksLabel, recipesLabel;
    private final MenuComponentMinimaList blocksList, recipesList;
    private final MenuComponentMinimalistButton addBlock, addRecipe;
    private boolean refreshNeeded = false;
    public MenuOverhaulTurbineConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul Turbine");
        minWidth = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Width").setIntFilter()).setTooltip("The minimum width of this multiblock");
        minLength = add(new MenuComponentMinimalistTextBox(sidebar.width, minWidth.height, 0, configuration.addon?0:48, "", true, "Minimum Length").setIntFilter()).setTooltip("The minimum length of this multiblock");
        maxSize = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Maximum Size").setIntFilter()).setTooltip("The maximum size of this multiblock");
        fluidPerBlade = add(new MenuComponentMinimalistTextBox(sidebar.width, minWidth.height, 0, configuration.addon?0:48, "", true, "Fluid Per Blade").setIntFilter()).setTooltip("The maximum fluid input per blade");
        throughputEfficiencyLeniencyMult = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Fluid Per Blade").setFloatFilter());
        throughputEfficiencyLeniencyThreshold = add(new MenuComponentMinimalistTextBox(sidebar.width, minWidth.height, 0, configuration.addon?0:48, "", true, "Fluid Per Blade").setFloatFilter());
        throughputFactor = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Fluid Per Blade").setFloatFilter());
        powerBonus = add(new MenuComponentMinimalistTextBox(sidebar.width, minWidth.height, 0, configuration.addon?0:48, "", true, "Fluid Per Blade").setFloatFilter());
        blocksLabel = add(new MenuComponentLabel(sidebar.width, minLength.y+minLength.height, 0, 48, "Blocks"));
        recipesLabel = add(new MenuComponentLabel(sidebar.width, minLength.y+minLength.height, 0, 48, "Recipes"));
        blocksList = add(new MenuComponentMinimaList(sidebar.width, blocksLabel.y+blocksLabel.height, 0, 0, 16));
        recipesList = add(new MenuComponentMinimaList(sidebar.width, recipesLabel.y+recipesLabel.height, 0, 0, 16));
        addBlock = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Block", true, true));
        addRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Recipe", true, true));
        addBlock.addActionListener((e) -> {
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.turbine.blocks.add(b);
            Core.configuration.overhaul.turbine.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        });
        addRecipe.addActionListener((e) -> {
            Recipe r = new Recipe("input_fluid", "output_fluid", 0, 0);
            configuration.overhaul.turbine.recipes.add(r);
            Core.configuration.overhaul.turbine.allRecipes.add(r);
            gui.open(new MenuRecipeConfiguration(gui, this, configuration, r));
        });
    }
    @Override
    public void onGUIOpened(){
        if(configuration.overhaul.turbine.blocks.size()>0)blocksLabel.text = "Blocks ("+configuration.overhaul.turbine.blocks.size()+")";
        if(configuration.overhaul.turbine.recipes.size()>0)recipesLabel.text = "Recipes ("+configuration.overhaul.turbine.recipes.size()+")";
        minWidth.text = configuration.overhaul.turbine.minWidth+"";
        minLength.text = configuration.overhaul.turbine.minLength+"";
        maxSize.text = configuration.overhaul.turbine.maxSize+"";
        fluidPerBlade.text = configuration.overhaul.turbine.fluidPerBlade+"";
        throughputEfficiencyLeniencyMult.text = configuration.overhaul.turbine.throughputEfficiencyLeniencyMult+"";
        throughputEfficiencyLeniencyThreshold.text = configuration.overhaul.turbine.throughputEfficiencyLeniencyThreshold+"";
        throughputFactor.text = configuration.overhaul.turbine.throughputFactor+"";
        powerBonus.text = configuration.overhaul.turbine.powerBonus+"";
        blocksList.components.clear();
        for(Block b : configuration.overhaul.turbine.blocks){
            blocksList.add(new MenuComponentBlock(b));
        }
        recipesList.components.clear();
        for(Recipe f : configuration.overhaul.turbine.recipes){
            recipesList.add(new MenuComponentRecipe(f));
        }
    }
    @Override
    public void onGUIClosed(){
        configuration.overhaul.turbine.minWidth = Integer.parseInt(minWidth.text);
        configuration.overhaul.turbine.minLength = Integer.parseInt(minLength.text);
        configuration.overhaul.turbine.maxSize = Integer.parseInt(maxSize.text);
        configuration.overhaul.turbine.fluidPerBlade = Integer.parseInt(fluidPerBlade.text);
        configuration.overhaul.turbine.throughputEfficiencyLeniencyMult = Float.parseFloat(throughputEfficiencyLeniencyMult.text);
        configuration.overhaul.turbine.throughputEfficiencyLeniencyThreshold = Float.parseFloat(throughputEfficiencyLeniencyThreshold.text);
        configuration.overhaul.turbine.throughputFactor = Float.parseFloat(throughputFactor.text);
        configuration.overhaul.turbine.powerBonus = Float.parseFloat(powerBonus.text);
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
        double w = gui.helper.displayWidth()-sidebar.width;
        minWidth.width = minLength.width = maxSize.width = fluidPerBlade.width = throughputEfficiencyLeniencyMult.width = throughputEfficiencyLeniencyThreshold.width = throughputFactor.width = powerBonus.width = w/4;
        minLength.y = fluidPerBlade.height = throughputEfficiencyLeniencyThreshold.height = powerBonus.y = minWidth.y+minWidth.height;;
        maxSize.x = fluidPerBlade.x = sidebar.width+w/4;
        throughputEfficiencyLeniencyMult.x = throughputEfficiencyLeniencyThreshold.x = sidebar.width+w*2/4;
        throughputFactor.x = powerBonus.x = sidebar.width+w*3/4;
        addBlock.width = addRecipe.width = blocksLabel.width = recipesLabel.width = blocksList.width = recipesList.width = w/2;
        addRecipe.x = recipesLabel.x = recipesList.x = blocksLabel.x+blocksLabel.width;
        addBlock.y = addRecipe.y = Core.helper.displayHeight()-addBlock.height;
        blocksList.height = recipesList.height = addBlock.y-(recipesLabel.y+recipesLabel.height);
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : blocksList.components){
            if(c instanceof MenuComponentBlock){
                if(button==((MenuComponentBlock) c).delete){
                    configuration.overhaul.turbine.blocks.remove(((MenuComponentBlock) c).block);
                    Core.configuration.overhaul.turbine.allBlocks.remove(((MenuComponentBlock) c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlock) c).edit){
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, ((MenuComponentBlock) c).block));
                    return;
                }
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : recipesList.components){
            if(c instanceof MenuComponentRecipe){
                if(button==((MenuComponentRecipe) c).delete){
                    configuration.overhaul.turbine.recipes.remove(((MenuComponentRecipe) c).recipe);
                    Core.configuration.overhaul.turbine.allRecipes.remove(((MenuComponentRecipe) c).recipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentRecipe) c).edit){
                    gui.open(new MenuRecipeConfiguration(gui, this, configuration, ((MenuComponentRecipe) c).recipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}