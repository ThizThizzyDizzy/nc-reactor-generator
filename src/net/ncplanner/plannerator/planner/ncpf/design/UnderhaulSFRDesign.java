package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.NCPFUnderhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
public class UnderhaulSFRDesign extends Design<NCPFUnderhaulSFRDesign>{
    public Fuel fuel;
    public Block[][][] design;
    public UnderhaulSFRDesign(NCPFFile file){
        super(file);
    }
    public UnderhaulSFRDesign(NCPFFile file, int x, int y, int z){
        this(file);
        design = new Block[x][y][z];
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        fuel = definition.fuel.copyTo(Fuel::new);
        design = copy3DArray(definition.design, Block::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.fuel = fuel;
        definition.design = design;
        super.convertToObject(ncpf);
    }
    
    
}