package planner.file.reader;

import simplelibrary.config2.Config;
public class NCPF8Reader extends NCPF9Reader {
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