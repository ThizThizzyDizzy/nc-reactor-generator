package planner.file.reader;

import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;

import java.io.InputStream;
public class NCPF7Reader extends NCPF8Reader {
    protected byte getTargetVersion() {
        return (byte) 7;
    }

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
            case 2:
                multiblock = readMultiblockOverhaulMSR(ncpf, data);
                break;
            case 3:
                multiblock = readMultiblockOverhaulTurbine(ncpf, data);
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
    protected void loadOverhaulFusionGeneratorBlocks(Config overhaul, Configuration configuration, boolean loadSettings) {
        // fusion did not exist in NCPF 7
    }
}