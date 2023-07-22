package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.editor.Editor;
public class SetMultiblockRecipeAction extends Action{
    private NCPFElement was = null;
    private final Editor editor;
    private final int recipeType;
    private final NCPFElement recipe;
    public SetMultiblockRecipeAction(Editor editor, int recipeType, NCPFElement recipe){
        this.editor = editor;
        this.recipeType = recipeType;
        this.recipe = recipe;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.getMultiblockRecipes()[recipeType];
        multiblock.setMultiblockRecipe(recipeType, recipe);
        editor.setMultiblockRecipe(recipeType, multiblock.getSpecificConfiguration().getMultiblockRecipes()[recipeType].indexOf(recipe));
    }
    @Override
    public void doUndo(Multiblock multiblock){
        multiblock.setMultiblockRecipe(recipeType, was);
        editor.setMultiblockRecipe(recipeType, multiblock.getSpecificConfiguration().getMultiblockRecipes()[recipeType].indexOf(was));
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList blocks){}
}