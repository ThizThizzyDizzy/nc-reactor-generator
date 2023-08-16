package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class CoolerModule extends BlockRulesModule{
    public int cooling;
    public CoolerModule(){
        super("nuclearcraft:underhaul_sfr:cooler");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        cooling = ncpf.getInteger("cooling");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("cooling", cooling);
        super.convertToObject(ncpf);
    }
    @Override
    public String getFunctionName(){
        return "Cooler";
    }
    @Override
    public String getStatsTooltip(){
        return "Cooling: "+cooling;
    }
}