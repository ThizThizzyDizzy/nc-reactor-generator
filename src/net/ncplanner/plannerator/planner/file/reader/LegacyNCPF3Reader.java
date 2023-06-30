package net.ncplanner.plannerator.planner.file.reader;

import net.ncplanner.plannerator.config2.Config;
public class LegacyNCPF3Reader extends LegacyNCPF4Reader{
    @Override
    protected byte getTargetVersion() {
        return (byte) 3;
    }

    @Override
    protected boolean readBladeStator(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block blade, Config config, String name) {
        return blade.bladeExpansion<1;
    }
}