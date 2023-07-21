package net.ncplanner.plannerator.planner.file.writer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class LegacyNCPFWriter extends FormatWriter{
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.LEGACY_NCPF;
    }
    @Override
    public void write(Project ncpf, OutputStream stream){
        Config header = Config.newConfig();
        header.set("version", LegacyNCPFFile.SAVE_VERSION);
        header.set("count", ncpf.designs.size());
        Config meta = Config.newConfig();
        for(String key : ncpf.metadata.metadata.keySet()){
            String value = ncpf.metadata.metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.set(key,value);
        }
        if(meta.properties().length>0){
            header.set("metadata", meta);
        }
        header.save(stream);
        ncpf.configuration.save(null, Config.newConfig()).save(stream);
        for(Multiblock m : ncpf.multiblocks){
            try{
                m.save(ncpf, ncpf.configuration, stream);
            }catch(MissingConfigurationEntryException ex){
                throw new RuntimeException(ex);
            }
        }
        try{
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public void writeToConfigs(LegacyNCPFFile ncpf, ArrayList<Config> configs){
        Config header = Config.newConfig();
        header.set("version", LegacyNCPFFile.SAVE_VERSION);
        header.set("count", ncpf.multiblocks.size());
        Config meta = Config.newConfig();
        for(String key : ncpf.metadata.keySet()){
            String value = ncpf.metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.set(key,value);
        }
        if(meta.properties().length>0){
            header.set("metadata", meta);
        }
        configs.add(header);
        configs.add(ncpf.configuration.save(null, Config.newConfig()));
        for(Multiblock m : ncpf.multiblocks){
            try{
                configs.add(m.saveToConfig(ncpf, ncpf.configuration));
            }catch(MissingConfigurationEntryException ex){
                throw new RuntimeException(ex);
            }
        }
    }
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return multi instanceof UnderhaulSFR
                || multi instanceof OverhaulSFR
                || multi instanceof OverhaulMSR
                || multi instanceof OverhaulTurbine
                || multi instanceof OverhaulFusionReactor;
    }
    public static void saveTexture(Config config, Image texture){
        saveTexture(config, "texture", texture);
    }
    public static void saveTexture(Config config, String keyName, Image texture){
        if(texture!=null){
            ConfigNumberList tex = new ConfigNumberList();
            tex.add(texture.getWidth());
            for(int x = 0; x<texture.getWidth(); x++){
                for(int y = 0; y<texture.getHeight(); y++){
                    tex.add(texture.getRGB(x, texture.getHeight()-y-1));//flip Y axis because GL
                }
            }
            config.set(keyName, tex);
        }
    }
}