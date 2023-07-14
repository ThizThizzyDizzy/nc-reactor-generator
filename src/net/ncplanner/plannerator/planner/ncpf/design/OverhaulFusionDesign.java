package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe;
public class OverhaulFusionDesign extends Design<OverhaulFusionDefinition>{
    public int innerRadius, coreSize, toroidWidth, liningThickness;
    public Recipe recipe;
    public CoolantRecipe coolantRecipe;
    public Block[][][] design;
    public BreedingBlanketRecipe[][][] breedingBlanketRecipes;
    public OverhaulFusionDesign(NCPFFile file){
        super(file);
    }
    public OverhaulFusionDesign(NCPFFile file, int innerRadius, int coreSize, int toroidWidth, int liningThickness){
        this(file);
        this.innerRadius = innerRadius;
        this.coreSize = coreSize;
        this.toroidWidth = toroidWidth;
        this.liningThickness = liningThickness;
        design = new Block[width()][height()][width()];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        NCPFList dims = ncpf.getNCPFList("dimensions");
        innerRadius = dims.getInteger(0);
        coreSize = dims.getInteger(1);
        toroidWidth = dims.getInteger(2);
        liningThickness = dims.getInteger(3);
        OverhaulFusionConfiguration config = definition.getConfiguration();
        design = new Block[width()][height()][width()];
        recipe = config.recipes.get(ncpf.getInteger("recipe"));
        coolantRecipe = config.coolantRecipes.get(ncpf.getInteger("coolant_recipe"));
        ncpf.getDefined3DArray("design", design, config.blocks);
        ncpf.getRecipe3DArray("block_recipes", breedingBlanketRecipes, design);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        OverhaulFusionConfiguration config = definition.getConfiguration();
        NCPFList dims = new NCPFList();
        dims.add(innerRadius);
        dims.add(coreSize);
        dims.add(toroidWidth);
        dims.add(liningThickness);
        ncpf.setNCPFList("dimensions", dims);
        ncpf.setInteger("recipe", config.recipes.indexOf(recipe));
        ncpf.setInteger("coolant_recipe", config.coolantRecipes.indexOf(coolantRecipe));
        ncpf.setDefined3DArray("design", design, config.blocks);
        ncpf.setRecipe3DArray("block_recipes", breedingBlanketRecipes, design);
        super.convertToObject(ncpf);
    }
    private int width(){
        return coreSize+2+innerRadius*2+toroidWidth*2+liningThickness*4+4;
    }
    private int height(){
        return liningThickness*2+coreSize+2;
    }
}