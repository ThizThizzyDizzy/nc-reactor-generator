package planner.file.reader;

import simplelibrary.config2.Config;
public class NCPF3Reader extends NCPF4Reader{
    @Override
    protected byte getTargetVersion() {
        return (byte) 3;
    }

    @Override
    protected boolean readBladeStator(multiblock.configuration.overhaul.turbine.Block blade, Config config, String name) {
        return blade.bladeExpansion<1;
    }
}