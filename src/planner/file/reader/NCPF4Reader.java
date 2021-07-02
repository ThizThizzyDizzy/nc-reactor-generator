package planner.file.reader;

import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import simplelibrary.config2.Config;
public class NCPF4Reader extends NCPF5Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 4;
    }

    @Override
    protected Configuration loadConfiguration(Config config){
        boolean partial = config.get("partial");
        Configuration configuration;
        if(partial)configuration = new PartialConfiguration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("overhaul")?config.get("underhaulVersion"):config.get("version"));
        else configuration = new Configuration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("overhaul")?config.get("underhaulVersion"):config.get("version"));
        configuration.addon = false;
        loadUnderhaulBlocks(config, configuration, configuration, true);
        if(config.hasProperty("overhaul")){
            configuration.overhaul = new OverhaulConfiguration();
            Config overhaul = config.get("overhaul");
            loadOverhaulSFRBlocks(overhaul, configuration, configuration, true, false);
            loadOverhaulMSRBlocks(overhaul, configuration, configuration, true, false);
            loadOverhaulTurbineBlocks(overhaul, configuration, configuration, true);
            // fusion did not exist in NCPF 4
        }
        return configuration;
    }
}