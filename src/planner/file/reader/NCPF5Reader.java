package planner.file.reader;

import simplelibrary.config2.Config;
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