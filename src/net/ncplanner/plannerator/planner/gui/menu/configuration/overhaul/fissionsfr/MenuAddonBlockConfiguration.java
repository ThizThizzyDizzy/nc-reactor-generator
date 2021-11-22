package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.planner.gui.menu.configuration.PartConfigurationMenu;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuAddonBlockConfiguration extends PartConfigurationMenu{
    public MenuAddonBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block parentBlock, Block block){
        super(gui, parent, configuration, parentBlock.getDisplayName());
        addList(()->{return "Block Recipes ("+block.recipes.size()+")";}, "New Recipe", ()->{
            BlockRecipe recipe = new BlockRecipe("nuclearcraft:input", "nuclearcraft:output");
            parentBlock.allRecipes.add(recipe);
            block.recipes.add(recipe);
            gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, parentBlock, recipe));
        }, (list)->{
            for(BlockRecipe recipe : block.recipes){
                list.add(new MenuComponentBlockRecipe(block, recipe, ()->{//edit
                    gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, parentBlock, recipe));
                }, ()->{//delete
                    parentBlock.allRecipes.remove(recipe);
                    block.recipes.remove(recipe);
                    refresh();
                }));
            }
        });
    }
}