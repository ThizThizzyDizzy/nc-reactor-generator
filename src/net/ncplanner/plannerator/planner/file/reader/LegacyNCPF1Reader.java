package net.ncplanner.plannerator.planner.file.reader;

import java.io.InputStream;
import java.util.List;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block;
public class LegacyNCPF1Reader extends LegacyNCPF2Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 1;
    }
    @Override
    protected synchronized Design readMultiblock(Project ncpf, InputStream in, RecoveryHandler recovery){
        Config data = Config.newConfig();
        data.load(in);
        Design design;
        int id = data.get("id");
        switch(id){
            case 0:
                design = readMultiblockUnderhaulSFR(ncpf, data, recovery);
                break;
            case 1:
                design = readMultiblockOverhaulSFR(ncpf, data, recovery);
                break;
            default:
                throw new IllegalArgumentException("Unknown Multiblock ID: "+id);
        }
        if(data.hasProperty("metadata")){
            Config metadata = data.get("metadata");
            for(String key : metadata.properties()){
                design.metadata.put(key, metadata.get(key));
            }
        }
        return design;
    }
    @Override
    protected void loadOverhaulMSRBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings, boolean loadingAddon, boolean isAddon, List<Block> additionalBlocks){
        // MSR reactors did not exist in NCPF 1
    }
}