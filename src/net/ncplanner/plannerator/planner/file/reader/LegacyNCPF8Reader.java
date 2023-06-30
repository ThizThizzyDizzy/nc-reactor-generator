package net.ncplanner.plannerator.planner.file.reader;

import net.ncplanner.plannerator.config2.Config;
public class LegacyNCPF8Reader extends LegacyNCPF9Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 8;
    }

    @Override
    protected int parseInputRate(Config blockCfg) {
        return blockCfg.hasProperty("input") ? 1 : 0;
    }
    @Override
    protected int parseOutputRate(Config blockCfg) {
        return blockCfg.hasProperty("output") ? 1 : 0;
    }
}