package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class NCPF7Reader extends NCPF8Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 7;
    }

    @Override
    protected synchronized Multiblock readMultiblock(NCPFFile ncpf, InputStream in, RecoveryHandler recovery) {
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
            case 2:
                multiblock = readMultiblockOverhaulMSR(ncpf, data, recovery);
                break;
            case 3:
                multiblock = readMultiblockOverhaulTurbine(ncpf, data, recovery);
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