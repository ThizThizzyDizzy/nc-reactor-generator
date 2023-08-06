package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule;
public class OverhaulSFRDesign extends Design<NCPFOverhaulSFRDesign> implements MultiblockDesign<OverhaulSFR>{
    public CoolantRecipe coolantRecipe;
    public BlockElement[][][] design;
    public Fuel[][][] fuels;
    public IrradiatorRecipe[][][] irradiatorRecipes;
    public OverhaulSFRDesign(NCPFFile file){
        super(file);
        definition = new NCPFOverhaulSFRDesign(file);
    }
    public OverhaulSFRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x+2][y+2][z+2];
        fuels = new Fuel[x+2][y+2][z+2];
        irradiatorRecipes = new IrradiatorRecipe[x+2][y+2][z+2];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        coolantRecipe = definition.coolantRecipe.copyTo(CoolantRecipe::new);
        match3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], file.getConfiguration(OverhaulSFRConfiguration::new).blocks);
        match3DArrayConditional(definition.blockRecipes, fuels = new Fuel[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement cell)->cell.fuels, (BlockElement cell)->cell.hasModule(FuelCellModule::new)||cell.parent!=null&&cell.parent.hasModule(FuelCellModule::new));
        match3DArrayConditional(definition.blockRecipes, irradiatorRecipes = new IrradiatorRecipe[definition.design.length][definition.design[0].length][definition.design[0][0].length], design, (BlockElement irradiator)->irradiator.irradiatorRecipes, (BlockElement irradiator)->irradiator.hasModule(IrradiatorModule::new)||irradiator.parent!=null&&irradiator.parent.hasModule(IrradiatorModule::new));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.coolantRecipe = coolantRecipe;
        definition.design = design;
        definition.blockRecipes = combine3DArraysInto(definition.blockRecipes = new NCPFElement[design.length][design[0].length][design[0][0].length], fuels, irradiatorRecipes);
        super.convertToObject(ncpf);
    }
    @Override
    public OverhaulSFR toMultiblock(){
        OverhaulSFR sfr = new OverhaulSFR(file.conglomeration, design.length-2, design[0].length-2, design[0][0].length-2, coolantRecipe);
        for(int x = 0; x<design.length; x++){
            for(int y = 0; y<design[x].length; y++){
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]==null)continue;
                    Block block = new Block(file.conglomeration, x, y, z, design[x][y][z]);
                    block.fuel = fuels[x][y][z];
                    block.irradiatorRecipe = irradiatorRecipes[x][y][z];
                    sfr.setBlock(x, y, z, block);
                }
            }
        }
        return sfr;
    }
    @Override
    public void convertElements(){
        OverhaulSFRConfiguration config = file.getConfiguration(OverhaulSFRConfiguration::new);
        convertElements(design, config);
        convertRecipes(design, fuels, (b)->b.fuels, config);
        convertRecipes(design, irradiatorRecipes, (b)->b.irradiatorRecipes, config);
        coolantRecipe = convertElement(coolantRecipe, config);
    }
}