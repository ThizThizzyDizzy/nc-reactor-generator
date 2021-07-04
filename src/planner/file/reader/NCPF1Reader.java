package planner.file.reader;

import java.io.InputStream;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
public class NCPF1Reader extends NCPF2Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 1;
    }

    @Override
    protected synchronized Multiblock readMultiblock(NCPFFile ncpf, InputStream in) {
        Config data = Config.newConfig();
        data.load(in);
        Multiblock multiblock;
        int id = data.get("id");
        switch(id){
            case 0:
                multiblock = readMultiblockUnderhaulSFR(ncpf, data);
                break;
            case 1:
                multiblock = readMultiblockOverhaulSFR(ncpf, data);
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