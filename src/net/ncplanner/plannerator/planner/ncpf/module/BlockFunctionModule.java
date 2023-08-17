package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.ConglomerationError;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public abstract class BlockFunctionModule extends NCPFSettingsModule{
    public BlockFunctionModule(String name){
        super(name);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new ConglomerationError("Block stats may not be overwritten! (Tried to conglomerate "+name+")");
    }
    public abstract String getFunctionName();
}