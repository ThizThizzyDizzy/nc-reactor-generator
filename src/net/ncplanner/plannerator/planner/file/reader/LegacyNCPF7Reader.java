package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class LegacyNCPF7Reader extends LegacyNCPF8Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 7;
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
            case 2:
                design = readMultiblockOverhaulMSR(ncpf, data, recovery);
                break;
            case 3:
                design = readMultiblockOverhaulTurbine(ncpf, data, recovery);
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
    protected void loadOverhaulFusionGeneratorBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings){
        // fusion did not exist in NCPF 7
    }
}