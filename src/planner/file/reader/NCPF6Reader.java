package planner.file.reader;

import multiblock.configuration.Configuration;
import simplelibrary.config2.Config;
public class NCPF6Reader extends NCPF7Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 6;
    }

    @Override
    protected void loadTurbineEfficiencyFactors(Config turbine, Configuration configuration) {
        configuration.overhaul.turbine.throughputEfficiencyLeniencyMult = .5f;
        configuration.overhaul.turbine.throughputEfficiencyLeniencyThreshold = .75f;
    }
}