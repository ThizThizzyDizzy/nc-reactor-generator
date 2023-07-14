package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulMSRDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule;
public class OverhaulMSRDesign extends Design<NCPFOverhaulMSRDesign>{
    public Block[][][] design;
    public Fuel[][][] fuels;
    public IrradiatorRecipe[][][] irradiatorRecipes;
    public HeaterRecipe[][][] heaterRecipes;
    public OverhaulMSRDesign(NCPFFile file){
        super(file);
    }
    public OverhaulMSRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new Block[x][y][z];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        design = copy3DArray(definition.design, Block::new);
        fuels = copy3DArrayConditional(definition.blockRecipes, design, Fuel::new, FuelVesselModule::new);
        irradiatorRecipes = copy3DArrayConditional(definition.blockRecipes, design, IrradiatorRecipe::new, IrradiatorModule::new);
        heaterRecipes = copy3DArrayConditional(definition.blockRecipes, design, HeaterRecipe::new, HeaterModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.design = design;
        definition.blockRecipes = combine3DArrays(fuels, irradiatorRecipes, heaterRecipes);
        super.convertToObject(ncpf);
    }
}