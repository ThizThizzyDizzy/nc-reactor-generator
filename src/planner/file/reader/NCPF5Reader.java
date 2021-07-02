package planner.file.reader;

import simplelibrary.config2.Config;
public class NCPF5Reader extends NCPF6Reader {
    protected byte getTargetVersion() {
        return (byte) 5;
    }

    protected float readOutputRatio(Config config, String name) {
        return config.getInt(name);
    }
}