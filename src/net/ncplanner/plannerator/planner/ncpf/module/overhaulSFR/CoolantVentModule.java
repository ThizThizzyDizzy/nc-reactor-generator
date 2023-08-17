package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoolantVentModule extends BlockFunctionModule{
    public boolean output;
    private NCPFElement otherVent;
    public CoolantVentModule(){
        super("nuclearcraft:overhaul_sfr:coolant_vent");
        addBoolean("output", ()->output, (v)->output = v, "Output");
    }
    public CoolantVentModule(boolean output){
        this();
        this.output = output;
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        for(NCPFElement elem : lst){
            elem.withModule(CoolantVentModule::new, (vent)->{
                if(vent.output!=output)otherVent = elem;
            });
        }
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(otherVent!=null){
            BlockElement thisOne = (BlockElement)parentObject;
            BlockElement other = (BlockElement)otherVent;
            if(output){
                thisOne.unToggled = other;
                other.toggled = thisOne;
            }else{
                thisOne.toggled = other;
                other.unToggled = thisOne;
            }
        }
    }
    @Override
    public String getFunctionName(){
        return "Coolant Vent";
    }
}