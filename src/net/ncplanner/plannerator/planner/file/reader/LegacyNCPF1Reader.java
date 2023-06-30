package net.ncplanner.plannerator.planner.file.reader;

import java.io.InputStream;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class LegacyNCPF1Reader extends LegacyNCPF2Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 1;
    }

    @Override
    protected synchronized Multiblock readMultiblock(LegacyNCPFFile ncpf, InputStream in, RecoveryHandler recovery) {
        Config data = Config.newConfig();
        data.load(in);
        Multiblock multiblock;
        int id = data.get("id");
        switch(id){
            case 0:
                multiblock = readMultiblockUnderhaulSFR(ncpf, data, recovery);
                break;
            case 1:
                multiblock = readMultiblockOverhaulSFR(ncpf, data, recovery);
                break;
            default:
                throw new IllegalArgumentException("Unknown Multiblock ID: "+id);
        }
        if(data.hasProperty("metadata")){
            Config metadata = data.get("metadata");
            for(String key : metadata.properties()){
                multiblock.metadata.put(key, metadata.get(key));
            }
        }
        return multiblock;
    }

    @Override
    protected void loadOverhaulMSRBlocks(Config overhaul, Configuration parent, Configuration configuration,
                                         boolean loadSettings, boolean loadingAddon) {
        // MSR reactors did not exist in NCPF 1
    }
}