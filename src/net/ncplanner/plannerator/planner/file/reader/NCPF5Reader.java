package net.ncplanner.plannerator.planner.file.reader;

import net.ncplanner.plannerator.config2.Config;
public class NCPF5Reader extends NCPF6Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 5;
    }

    @Override
    protected float readOutputRatio(Config config, String name) {
        return config.getInt(name);
    }
}