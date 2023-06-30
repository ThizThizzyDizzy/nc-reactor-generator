package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFOverhaulMSRConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
public class NCPFOverhaulMSRDesign extends NCPFCuboidalMultiblockDesign{
    public NCPFElement coolantRecipe;
    private NCPFElement[][][] blockRecipes;
    public NCPFOverhaulMSRDesign(){
        super("nuclearcraft:overhaul_msr");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf, NCPFFile file){
        super.convertFromObject(ncpf, file);
        blockRecipes = new NCPFElement[design.length][design[0].length][design[1].length];
        NCPFOverhaulMSRConfiguration config = (NCPFOverhaulMSRConfiguration) getConfiguration(file);
        NCPFList des = ncpf.getNCPFList("design");
        NCPFList recipes = ncpf.getNCPFList("block_recipes");
        int i = -1;
        int r = -1;
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    design[x][y][z] = config.blocks.get(des.getInteger(++i));
                    if(design[x][y][z].hasModule(NCPFBlockRecipesModule::new)){
                        blockRecipes[x][y][z] = design[x][y][z].getModule(NCPFBlockRecipesModule::new).recipes.get(recipes.getInteger(++r));
                    }
                }
            }
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf, NCPFFile file){
        super.convertToObject(ncpf, file);
        NCPFOverhaulMSRConfiguration config = (NCPFOverhaulMSRConfiguration) getConfiguration(file);
        NCPFList<Integer> des = new NCPFList<>();
        NCPFList<Integer> recipes = new NCPFList<>();
        for(int x = 0; x<=design.length; x++){
            for(int y = 0; y<=design[x].length; y++){
                for(int z = 0; z<=design[z].length; z++){
                    des.add(config.blocks.indexOf(design[x][y][z]));
                    if(design[x][y][z].hasModule(NCPFBlockRecipesModule::new)){
                        recipes.add(design[x][y][z].getModule(NCPFBlockRecipesModule::new).recipes.indexOf(blockRecipes[x][y][z]));
                    }
                }
            }
        }
        ncpf.setNCPFList("design", des);
        ncpf.setNCPFList("block_recipes", recipes);
    }
}