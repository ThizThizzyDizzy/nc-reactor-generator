package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public abstract class BlockFunctionModule extends NCPFModule{
    public BlockFunctionModule(String name){
        super(name);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        throw new UnsupportedOperationException("Block stats may not be overwritten!");
    }
}