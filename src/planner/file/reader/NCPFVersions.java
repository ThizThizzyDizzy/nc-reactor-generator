package planner.file.reader;

import simplelibrary.config2.Config;

public final class NCPFVersions {
    private NCPFVersions() { }

    public static class NCPF11 extends NCPFReaderBase {
        public NCPF11() {
            super((byte) 11);
        }

        @Override
        protected int readRuleBlockIndex(Config config, String name) {
            return config.get(name);
        }
    }

    public static class NCPF10 extends NCPFReaderBase {
        public NCPF10() {
            super((byte) 10);
        }

        @Override
        protected int readRuleBlockIndex(Config config, String name) {
            return (byte) config.get(name);
        }
    }
}
