package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulMSRDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule;
public class OverhaulMSRDesign extends Design<NCPFOverhaulMSRDesign>{
    public BlockElement[][][] design;
    public Fuel[][][] fuels;
    public IrradiatorRecipe[][][] irradiatorRecipes;
    public HeaterRecipe[][][] heaterRecipes;
    public OverhaulMSRDesign(NCPFFile file){
        super(file);
    }
    public OverhaulMSRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x][y][z];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        match3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], file.getConfiguration(OverhaulMSRConfiguration::new).blocks);
        match3DArrayConditional(definition.blockRecipes, fuels = new Fuel[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement vessel)->vessel.fuels, (BlockElement vessel)->vessel.hasModule(FuelVesselModule::new)||vessel.parent!=null&&vessel.parent.hasModule(FuelVesselModule::new));
        match3DArrayConditional(definition.blockRecipes, irradiatorRecipes = new IrradiatorRecipe[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement irradiator)->irradiator.irradiatorRecipes, (BlockElement irradiator)->irradiator.hasModule(IrradiatorModule::new)||irradiator.parent!=null&&irradiator.parent.hasModule(IrradiatorModule::new));
        match3DArrayConditional(definition.blockRecipes, heaterRecipes = new HeaterRecipe[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement heater)->heater.heaterRecipes, (BlockElement heater)->heater.hasModule(HeaterModule::new)||heater.parent!=null&&heater.parent.hasModule(HeaterModule::new));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.design = design;
        definition.blockRecipes = combine3DArrays(fuels, irradiatorRecipes, heaterRecipes);
        super.convertToObject(ncpf);
    }
}