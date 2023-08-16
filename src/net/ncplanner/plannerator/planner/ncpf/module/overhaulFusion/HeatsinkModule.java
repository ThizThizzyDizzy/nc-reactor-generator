package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class HeatsinkModule extends BlockRulesModule{
    public int cooling;
    public HeatsinkModule(){
        super("plannerator:fusion_test:heatsink");
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
        return "Heatsink";
    }
    @Override
    public String getTooltip(){
        return "Cooling: "+cooling;
    }
}