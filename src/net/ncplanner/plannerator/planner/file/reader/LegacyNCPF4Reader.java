package net.ncplanner.plannerator.planner.file.reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
public class LegacyNCPF4Reader extends LegacyNCPF5Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 4;
    }

    @Override
    protected void loadConfiguration(Project project, Config config){
        boolean partial = config.getBoolean("partial");
        String name = config.getString("name");
        String version = config.getString("version");
        String underhaulVersion = config.getString("underhaulVersion");
        boolean addon = false;
        loadUnderhaulBlocks(project.configuration, config, true);
        List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement> overhaulSFRAdditionalBlocks = new ArrayList<>();
        List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement> overhaulMSRAdditionalBlocks = new ArrayList<>();
        if(config.hasProperty("overhaul")){
            Config overhaul = config.getConfig("overhaul");
            loadOverhaulSFRBlocks(null, project.configuration, overhaul, true, false, addon, overhaulSFRAdditionalBlocks);
            loadOverhaulMSRBlocks(null, project.configuration, overhaul, true, false, addon, overhaulMSRAdditionalBlocks);
            loadOverhaulTurbineBlocks(project.configuration, overhaul, true);
            // fusion did not exist in NCPF 4
        }
        project.configuration.withConfiguration(UnderhaulSFRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = underhaulVersion;
        });
        project.configuration.withConfiguration(OverhaulSFRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulMSRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulTurbineConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        if(config.hasProperty("addons")){
            ConfigList addons = config.getConfigList("addons");
            for(int i = 0; i<addons.size(); i++){
                project.addons.add(loadAddon(project, addons.get(i)));
            }
        }
        if(!overhaulSFRAdditionalBlocks.isEmpty())project.configuration.getConfiguration(OverhaulSFRConfiguration::new).blocks.addAll(overhaulSFRAdditionalBlocks);
        if(!overhaulMSRAdditionalBlocks.isEmpty())project.configuration.getConfiguration(OverhaulMSRConfiguration::new).blocks.addAll(overhaulMSRAdditionalBlocks);
        project.conglomerate();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule rule : underhaulPostLoadMap.keySet()){
            int index = underhaulPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockReference(project.getConfiguration(UnderhaulSFRConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
            int index = overhaulSFRPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference(project.getConfiguration(OverhaulSFRConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
            int index = overhaulMSRPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference(project.getConfiguration(OverhaulMSRConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
            int index = overhaulTurbinePostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockReference(project.getConfiguration(OverhaulTurbineConfiguration::new).blocks.get(index-1));
            }
        }
        //combine underhaul active coolers into one
        net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement activeCooler = null;
        for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement> it = project.configuration.getConfiguration(UnderhaulSFRConfiguration::new).blocks.iterator(); it.hasNext();){
            net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement block = it.next();
            if(block.activeCooler!=null){
                if(activeCooler==null){
                    activeCooler = block;
                    continue;
                }
                activeCooler.activeCoolerRecipes.addAll(block.activeCoolerRecipes);
                it.remove();
            }
        }
        //do the same for the conglomeration
        activeCooler = null;
        for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement> it = project.getConfiguration(UnderhaulSFRConfiguration::new).blocks.iterator(); it.hasNext();){
            net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement block = it.next();
            if(block.activeCooler!=null){
                if(activeCooler==null){
                    activeCooler = block;
                    continue;
                }
                activeCooler.activeCoolerRecipes.addAll(block.activeCoolerRecipes);
                it.remove();
            }
        }
    }
}