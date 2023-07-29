package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFUnderhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
public class UnderhaulSFRDesign extends Design<NCPFUnderhaulSFRDesign> implements MultiblockDesign<UnderhaulSFR>{
    public Fuel fuel;
    public BlockElement[][][] design;
    public UnderhaulSFRDesign(NCPFFile file){
        super(file);
        definition = new NCPFUnderhaulSFRDesign(file);
    }
    public UnderhaulSFRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new BlockElement[x+2][y+2][z+2];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        fuel = copy(definition.fuel, Fuel::new);
        copy3DArray(definition.design, design = new BlockElement[definition.design.length][definition.design[0].length][definition.design[0][0].length], BlockElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.fuel = fuel;
        definition.design = design;
        super.convertToObject(ncpf);
    }
    @Override
    public UnderhaulSFR toMultiblock(){
        UnderhaulSFR sfr = new UnderhaulSFR(file.conglomeration, design.length-2, design[0].length-2, design[0][0].length-2, fuel);
        for(int x = 0; x<design.length; x++){
            for(int y = 0; y<design[x].length; y++){
                for(int z = 0; z<design[x][y].length; z++){
                    if(design[x][y][z]==null)continue;
                    sfr.setBlock(x, y, z, new Block(file.conglomeration, x, y, z, design[x][y][z]));
                }
            }
        }
        return sfr;
    }
}