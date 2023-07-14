package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulTurbineDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
public class OverhaulTurbineDesign extends Design<NCPFOverhaulTurbineDesign>{
    public Recipe recipe;
    public Block[][][] design;
    public OverhaulTurbineDesign(NCPFFile file){
        super(file);
    }
    public OverhaulTurbineDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new Block[x][y][z];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        recipe = definition.recipe.copyTo(Recipe::new);
        design = copy3DArray(definition.design, Block::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.recipe = recipe;
        definition.design = design;
        super.convertToObject(ncpf);
    }
}