package net.ncplanner.plannerator.planner.file.reader;

import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
public class LegacyNCPF6Reader extends LegacyNCPF7Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 6;
    }
    
    @Override
    protected void loadTurbineEfficiencyFactors(Config turbine, OverhaulTurbineConfiguration configuration){
        configuration.settings.throughputEfficiencyLeniencyMultiplier = .5f;
        configuration.settings.throughputEfficiencyLeniencyThreshold = .75f;
    }
}