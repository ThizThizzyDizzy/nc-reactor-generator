package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class PortModule extends BlockFunctionModule{
    public boolean output;
    public PortModule(){
        super("nuclearcraft:overhaul_msr:port");
        addBoolean("output", ()->output, (v)->output = v, "Output");
    }
    public PortModule(boolean output){
        this();
        this.output = output;
    }
    @Override
    public String getFunctionName(){
        return (output?"Output":"Input")+" Port";
    }
}