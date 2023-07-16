package net.ncplanner.plannerator.planner.file.reader;

import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block;
public class LegacyNCPF3Reader extends LegacyNCPF4Reader{
    @Override
    protected byte getTargetVersion() {
        return (byte) 3;
    }
    @Override
    protected boolean readBladeStator(Block blade, Config config, String name){
        return config.getFloat("expansion")<1;
    }
}