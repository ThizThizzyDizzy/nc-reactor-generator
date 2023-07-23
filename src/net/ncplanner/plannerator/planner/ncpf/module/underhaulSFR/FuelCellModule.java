package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class FuelCellModule extends BlockFunctionModule{
    public FuelCellModule(){
        super("nuclearcraft:underhaul_sfr:fuel_cell");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Fuel Cell";
    }
}