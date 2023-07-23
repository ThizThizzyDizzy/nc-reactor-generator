package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ModeratorModule extends BlockFunctionModule implements ElementStatsModule{
    public int flux;
    public float efficiency;
    public ModeratorModule(){
        super("nuclearcraft:overhaul_sfr:moderator");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        flux = ncpf.getInteger("flux");
        efficiency = ncpf.getFloat("efficiency");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("flux", flux);
        ncpf.setFloat("efficiency", efficiency);
    }
    @Override
    public String getFunctionName(){
        return "Moderator";
    }
    @Override
    public String getTooltip(){
        return "Flux: "+flux+"\n"
                + "Efficiency: "+efficiency;
    }
}