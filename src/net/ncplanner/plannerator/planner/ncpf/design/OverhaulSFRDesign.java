package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule;
public class OverhaulSFRDesign extends Design<NCPFOverhaulSFRDesign>{
    public CoolantRecipe coolantRecipe;
    public Block[][][] design;
    public Fuel[][][] fuels;
    public IrradiatorRecipe[][][] irradiatorRecipes;
    public OverhaulSFRDesign(NCPFFile file){
        super(file);
    }
    public OverhaulSFRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new Block[x][y][z];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        coolantRecipe = definition.coolantRecipe.copyTo(CoolantRecipe::new);
        design = copy3DArray(definition.design, Block::new);
        fuels = copy3DArrayConditional(definition.blockRecipes, design, Fuel::new, FuelCellModule::new);
        irradiatorRecipes = copy3DArrayConditional(definition.blockRecipes, design, IrradiatorRecipe::new, IrradiatorModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.coolantRecipe = coolantRecipe;
        definition.design = design;
        definition.blockRecipes = combine3DArrays(fuels, irradiatorRecipes);
        super.convertToObject(ncpf);
    }
}