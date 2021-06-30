package planner.file.reader;

import simplelibrary.config2.Config;

public class NCPF10Reader extends NCPF11Reader {
    protected int readRuleBlockIndex(Config config, String name) {
        return (byte) config.get(name);
    }
}